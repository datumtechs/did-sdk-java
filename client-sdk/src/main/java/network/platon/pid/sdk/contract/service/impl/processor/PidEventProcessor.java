package network.platon.pid.sdk.contract.service.impl.processor;


import com.platon.bech32.Bech32;
import com.platon.crypto.Hash;
import com.platon.protocol.core.DefaultBlockParameterNumber;
import com.platon.protocol.core.methods.response.*;
import com.platon.utils.Numeric;
import network.platon.pid.common.enums.ContractStatusEnum;
import network.platon.pid.contract.Pid;
import network.platon.pid.contract.client.RetryableClient;
import network.platon.pid.sdk.base.dto.*;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.commonConstant;
import network.platon.pid.sdk.enums.PidAttrType;
import network.platon.pid.sdk.exception.ContractException;
import network.platon.pid.sdk.resolve.dto.DecodeResult;
import network.platon.pid.sdk.utils.PidUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-11 14:37
 */
public class PidEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(PidEventProcessor.class);

    private static final HashMap<String, String> topicMap;

    static {
        // initialize the event topic
        topicMap = new HashMap<String, String>();
        // EVENT Name: `PIDAttributeChanged` on Pid
        topicMap.put(
                PidConst.PID_EVENT_ATTRIBUTE_CHANGE_RLP,
                PidConst.PID_EVENT_ATTRIBUTE_CHANGE_STR
        );
        topicMap.put(
                PidConst.PID_EVENT_ERROR_RLP,
                PidConst.PID_EVETN_ERROR_STR
        );

    }


    public static DocumentData processBlockReceipt(Pid Pid, String pid, BigInteger blockNumber) {

        log.debug("[QueryDocument] process block receipt with pid: {}", pid);

        DocumentData document = new DocumentData();
        document.setId(pid);

        BigInteger previousBlock = blockNumber;
        while (!BigInteger.ZERO.equals(previousBlock)) {

            BigInteger currentBlockNumber = previousBlock;
            PlatonBlock latestBlock = null;

            try {
                // Fetch the block with currentBlockNumber
                latestBlock = RetryableClient.getWeb3j()
                        .platonGetBlockByNumber(new DefaultBlockParameterNumber(currentBlockNumber), true).send();
            } catch (IOException e) {
                log.error("[QueryDocument] platonGetBlockByNumber error,  currentBlockNumber:{}, pid: {}, exception: {}", currentBlockNumber, pid, e);
                throw new ContractException(ContractStatusEnum.C_NETWOEK_EXCEPTION);
            }
            if (latestBlock == null) {
                log.error("[QueryDocument] platonGetBlockByNumber latestBlock is null,  currentBlockNumber:{}, pid: {}", currentBlockNumber, pid);
                throw new ContractException(ContractStatusEnum.C_NETWOEK_EXCEPTION);
            }

            // Fetch the tx list with block
            List<Transaction> transList = latestBlock.getBlock().getTransactions().stream()
                    .map(transactionResult -> (Transaction) transactionResult.get()).collect(Collectors.toList());

            // Zero number reset the value of previousBlock
            previousBlock = BigInteger.ZERO;

            try {
                for (Transaction transaction : transList) {

                    String transHash = transaction.getHash();
                    PlatonGetTransactionReceipt platonReceipt = RetryableClient.getWeb3j().platonGetTransactionReceipt(transHash).send();
                    Optional<TransactionReceipt> value = platonReceipt.getTransactionReceipt();
                    TransactionReceipt receipt = null;
                    if(value.isPresent()) {
                    	receipt = value.get();
                    } else {
                    	continue;
                    }
                    List<Log> logs = platonReceipt.getResult().getLogs();

                    for (Log log : logs) {
                        // Process logs data and extract document attribute
                        DecodeResult returnValue = processEventLog(Pid, log, receipt, document);
                        if (returnValue.getResultStatus().equals(ContractStatusEnum.C_STATUS_SUCCESS)) {
                            if (returnValue.getPreviousBlock().compareTo(currentBlockNumber) == 0) {
                                continue;
                            }
                            // Target number reset the value of previousBlock
                            previousBlock = returnValue.getPreviousBlock();
                        }
                    }
                }
            } catch (IOException e) {
                log.error("[QueryDocument] get TransactionReceipt failed, the pid: {}, exception: {}", pid, e);
                throw new ContractException(ContractStatusEnum.C_NETWOEK_EXCEPTION);
            }
        }
        return document;
    }

    private static DecodeResult processEventLog(
            Pid Pid,
            Log log, TransactionReceipt receipt,
            DocumentData document) {

        String topic = log.getTopics().get(0);
        String event = topicMap.get(topic);

        if (StringUtils.isNotBlank(event)) {
            switch (event) {
                case PidConst.PID_EVENT_ATTRIBUTE_CHANGE_STR:
                    return processAttributeEvent(Pid, receipt, document);
            }
        }
        DecodeResult response = new DecodeResult();
        response.setDecodeEventLogStatus(ContractStatusEnum.C_EVENT_NULL);
        return response;
    }

    private static DecodeResult processAttributeEvent(Pid Pid, TransactionReceipt receipt,
                                                      DocumentData document) {

        List<Pid.PIDAttributeChangeEventResponse> eventlog =
                Pid.getPIDAttributeChangeEvents(receipt);

        DecodeResult response = new DecodeResult();

        if (CollectionUtils.isEmpty(eventlog)) {
            response.setDecodeEventLogStatus(ContractStatusEnum.C_EVENTLOG_NULL);
            return response;
        }
        BigInteger previousBlock = BigInteger.ZERO;

        //        event PIDAttributeChange (
        //                address indexed identity,
        //                uint8 fieldKey,
        //                string fieldValue,
        //                uint256 blockNumber,
        //                string  updateTime
        //        );
        for (Pid.PIDAttributeChangeEventResponse res : eventlog) {
            if (StringUtils.isBlank(res.identity)) {
                response.setDecodeEventLogStatus(ContractStatusEnum.C_RES_NULL);
                return response;
            }

            String identityHash = res.identity;

            String address = PidUtils.convertPidToAddressStr(document.getId());

            // the `topic` is already sha3(rlp(topic)) encoded value
            String pidTopicHash = Numeric.toHexStringWithPrefixZeroPadded(Numeric.toBigInt(Bech32.addressDecode(address)), 64);
            if (!StringUtils.equals(pidTopicHash, identityHash)) {
                response.setDecodeEventLogStatus(ContractStatusEnum.C_KEY_NOT_MATCH);
                return response;
            }

            // setting the document properties
            if (null == document.getUpdated() || document.getUpdated().length() == 0) {
                document.setUpdated(res.updateTime);
            }
            BigInteger key = res.fieldKey;
            String value = res.fieldValue;
            previousBlock = res.blockNumber;

            // to assemble properties of Document
            assembleDocumentAttribute(key, value, document);
        }

        response.setPreviousBlock(previousBlock);
        response.setDecodeEventLogStatus(ContractStatusEnum.C_STATUS_SUCCESS);
        return response;
    }

    private static void assembleDocumentAttribute(BigInteger key, String value, DocumentData document) {

        switch (PidAttrType.findPidAttr(key)) {
            case CREATED:
                assembleCreated(value, document);
                break;
            case AUTH:
                assembleAuthentications(value, document);
                break;
            case PUBLICKEY:
                assemblePublicKeys(value, document);
                break;
            case SERVICE:
                assembleServices(value, document);
                break;
            default:
                break;
        }
    }

    // the publicKey format on contract, example:
    //          key: PidAttrType.PUBLICKEY.getCode
    //          value:   {publicKey}|{controller}|{type}|{status}|{index}
    private static void assemblePublicKeys(String value, DocumentData document) {

        log.debug("Call method assemblePublicKeys() parameter, value:{}, document:{}",
                value, document);

        if (StringUtils.isBlank(value)) {
            return;
        }
        String[] valueArray = StringUtils.splitByWholeSeparator(value, commonConstant.SEPARATOR_PIPELINE);
        if (valueArray.length != PidConst.PID_PUBLICKEY_VALUE_MEM_LEN) {
            return;
        }

        String publicKey = valueArray[0];
        // convert address to pid
        String controller = PidUtils.convertAddressStrToPid(valueArray[1]);
        String type = valueArray[2];
        String status = valueArray[3];
        String index = valueArray[4];

        List<DocumentPubKeyData> publicKeys = document.getPublicKey();
        for (DocumentPubKeyData pub : publicKeys) {
            // The latest Attribute of the publicKey has been obtained,
            // the old Attribute does not need to be collected.
            if (StringUtils.equals(pub.getPublicKeyHex(), publicKey)) {
                return;
            }
        }
        DocumentPubKeyData pubKey = new DocumentPubKeyData();
        pubKey.setId(
                new StringBuilder()
                        .append(document.getId())
                        .append(commonConstant.SEPARATOR_DOCUMENT_PUBLICKEY_ID)
                        .append(index).toString());
        pubKey.setType(type);
        pubKey.setController(controller);
        pubKey.setPublicKeyHex(publicKey);
        pubKey.setStatus(status);
        document.getPublicKey().add(pubKey);
    }


    // the publicKey format on contract, example:
    //          key: PidAttrType.AUTH.getCode
    //          value: {publicKey}|{controller}|{status}
    private static void assembleAuthentications(String value, DocumentData document) {

        log.debug("[QueryDocument] call method assembleAuthentications() parameter, value:{}, document:{}",
                value, document);

        if (StringUtils.isBlank(value)) {
            return;
        }
        String[] valueArray = StringUtils.splitByWholeSeparator(value, commonConstant.SEPARATOR_PIPELINE);
        if (valueArray.length != PidConst.PID_AUTH_VALUE_MEM_LEN) {
            return;
        }
        String publicKey = valueArray[0];
        // convert address to pid
        String controller = PidUtils.convertAddressStrToPid(valueArray[1]);
        String status = valueArray[2];
        List<DocumentAuthData> authList = document.getAuthentication();

        for (DocumentAuthData auth : authList) {
            // The latest Attribute of the publicKey has been obtained,
            // the old Attribute does not need to be collected.
            if (StringUtils.equals(auth.getPublicKeyHex(), publicKey)) {
                return;
            }
        }
        DocumentAuthData auth = new DocumentAuthData();
        auth.setController(controller);
        auth.setPublicKeyHex(publicKey);
        auth.setStatus(status);
        document.getAuthentication().add(auth);
    }

    // the service format on contract, example:
    //          key: PidAttrType.SERVICE.getCode,  value:  {id}|{type}|{endPoint}|{status}
    private static void assembleServices(String value, DocumentData document) {

        log.debug("[QueryDocument] call method assembleServices() parameter, value:{}, document:{}",
                value, document);

        if (StringUtils.isBlank(value)) {
            return;
        }
        String[] valueArray = StringUtils.splitByWholeSeparator(value, commonConstant.SEPARATOR_PIPELINE);
        if (valueArray.length != PidConst.PID_SERVICE_VALUE_MEM_LEN) {
            return;
        }
        String id = valueArray[0];
        String type = valueArray[1];
        String endPoint = valueArray[2];
        String status = valueArray[3];

        String serviceId = new StringBuilder()
                .append(document.getId())
                .append(commonConstant.SEPARATOR_POUND)
                .append(id)
                .toString();

        List<DocumentServiceData> services = document.getService();
        // Deduplication based on Id and type
        for (DocumentServiceData ser : services) {
            if (StringUtils.equals(ser.getId(), serviceId)
                    || StringUtils.equals(ser.getType(), type)) {
                return;
            }
        }
        DocumentServiceData service = new DocumentServiceData();
        service.setId(serviceId);
        service.setType(type);
        service.setServiceEndpoint(endPoint);
        service.setStatus(status);
        document.getService().add(service);
    }

    private static void assembleCreated(String value, DocumentData document) {
        document.setCreated(Long.valueOf(value));
    }

}
