package network.platon.did.sdk.contract.service.impl.processor;


import com.platon.bech32.Bech32;
import com.platon.protocol.core.DefaultBlockParameterNumber;
import com.platon.protocol.core.methods.response.*;
import com.platon.utils.Numeric;
import network.platon.did.common.enums.ContractStatusEnum;
import network.platon.did.contract.Did;
import network.platon.did.contract.client.RetryableClient;
import network.platon.did.sdk.base.dto.*;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.commonConstant;
import network.platon.did.sdk.enums.DidAttrType;
import network.platon.did.sdk.exception.ContractException;
import network.platon.did.sdk.resolve.dto.DecodeResult;
import network.platon.did.sdk.utils.DidUtils;
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
public class DidEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(DidEventProcessor.class);

    private static final HashMap<String, String> topicMap;

    static {
        // initialize the event topic
        topicMap = new HashMap<String, String>();
        // EVENT Name: `DIDAttributeChanged` on Did
        topicMap.put(
                DidConst.DID_EVENT_ATTRIBUTE_CHANGE_TOPIC,
                DidConst.DID_EVENT_ATTRIBUTE_CHANGE_STR
        );
    }


    public static DocumentData processBlockReceipt(Did Did, String did, BigInteger blockNumber) {

        log.debug("[QueryDocument] process block receipt with did: {}", did);

        DocumentData document = new DocumentData();
        document.setId(did);

        BigInteger previousBlock = blockNumber;
        while (!BigInteger.ZERO.equals(previousBlock)) {

            BigInteger currentBlockNumber = previousBlock;
            PlatonBlock latestBlock = null;

            try {
                // Fetch the block with currentBlockNumber
                latestBlock = RetryableClient.getWeb3j()
                        .platonGetBlockByNumber(new DefaultBlockParameterNumber(currentBlockNumber), true).send();
            } catch (IOException e) {
                log.error("[QueryDocument] platonGetBlockByNumber error,  currentBlockNumber:{}, did: {}, exception: {}", currentBlockNumber, did, e);
                throw new ContractException(ContractStatusEnum.C_NETWOEK_EXCEPTION);
            }
            if (latestBlock == null) {
                log.error("[QueryDocument] platonGetBlockByNumber latestBlock is null,  currentBlockNumber:{}, did: {}", currentBlockNumber, did);
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

                    if (!StringUtils.equals(receipt.getTo(), Did.getContractAddress())) {
                        continue;
                    }

                    List<Log> logs = platonReceipt.getResult().getLogs();

                    for (Log log : logs) {
                        // Process logs data and extract document attribute
                        DecodeResult returnValue = processEventLog(Did, log, receipt, document);
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
                log.error("[QueryDocument] get TransactionReceipt failed, the did: {}, exception: {}", did, e);
                throw new ContractException(ContractStatusEnum.C_NETWOEK_EXCEPTION);
            }
        }
        return document;
    }

    private static DecodeResult processEventLog(
            Did Did,
            Log log, TransactionReceipt receipt,
            DocumentData document) {

        String topic = log.getTopics().get(0);
        String event = topicMap.get(topic);

        if (StringUtils.isNotBlank(event)) {
            switch (event) {
                case DidConst.DID_EVENT_ATTRIBUTE_CHANGE_STR:
                    return processAttributeEvent(Did, receipt, document);
            }
        }
        DecodeResult response = new DecodeResult();
        response.setDecodeEventLogStatus(ContractStatusEnum.C_EVENT_NULL);
        return response;
    }

    private static DecodeResult processAttributeEvent(Did Did, TransactionReceipt receipt,
                                                      DocumentData document) {

        List<Did.DIDAttributeChangeEventResponse> eventlog =
                Did.getDIDAttributeChangeEvents(receipt);

        DecodeResult response = new DecodeResult();

        if (CollectionUtils.isEmpty(eventlog)) {
            response.setDecodeEventLogStatus(ContractStatusEnum.C_EVENTLOG_NULL);
            return response;
        }
        BigInteger previousBlock = BigInteger.ZERO;

        //        event DIDAttributeChange (
        //                address indexed identity,
        //                uint8 fieldKey,
        //                string fieldValue,
        //                uint256 blockNumber,
        //                string  updateTime
        //        );
        for (Did.DIDAttributeChangeEventResponse res : eventlog) {
            if (StringUtils.isBlank(res.identity)) {
                response.setDecodeEventLogStatus(ContractStatusEnum.C_RES_NULL);
                return response;
            }

            String identity = res.identity;

            String address = DidUtils.convertDidToAddressStr(document.getId());

            if (!StringUtils.equals(address, identity)) {
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

        switch (DidAttrType.findDidAttr(key)) {
            case CREATED:
                assembleCreated(value, document);
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
    //          key: DidAttrType.PUBLICKEY.getCode
    //          value:   {publicKey}|{type}|{index}|{status}
    private static void assemblePublicKeys(String value, DocumentData document) {

        log.debug("Call method assemblePublicKeys() parameter, value:{}, document:{}",
                value, document);

        if (StringUtils.isBlank(value)) {
            return;
        }

        String[] valueArray = StringUtils.splitByWholeSeparator(value, commonConstant.SEPARATOR_PIPELINE);
        if (valueArray.length != DidConst.DID_PUBLICKEY_VALUE_MEM_LEN) {
            return;
        }

        String publicKey = valueArray[0];
        String type = valueArray[1];
        String index = valueArray[2];
        String status = valueArray[3];

        List<DocumentPubKeyData> publicKeys = document.getPublicKey();
        for (DocumentPubKeyData pub : publicKeys) {
            // The latest Attribute of the publicKey has been obtained,
            // the old Attribute does not need to be collected.
            if (StringUtils.equals(pub.getPublicKeyHex(), publicKey)) {
                return;
            }

            String[] valueIdArray = StringUtils.splitByWholeSeparator(pub.getId(), commonConstant.SEPARATOR_DOCUMENT_PUBLICKEY_ID);

            if (StringUtils.equals(valueIdArray[1], index)) {
                return;
            }

        }
        DocumentPubKeyData pubKey = new DocumentPubKeyData();
        pubKey.setId(new StringBuilder()
                .append(document.getId())
                .append(commonConstant.SEPARATOR_DOCUMENT_PUBLICKEY_ID)
                .append(index).toString());
        pubKey.setType(type);
        pubKey.setPublicKeyHex(publicKey);
        pubKey.setStatus(status);
        document.getPublicKey().add(pubKey);
    }

    // the service format on contract, example:
    //          key: DidAttrType.SERVICE.getCode,  value:  {id}|{type}|{endPoint}|{status}
    private static void assembleServices(String value, DocumentData document) {

        log.debug("[QueryDocument] call method assembleServices() parameter, value:{}, document:{}",
                value, document);

        if (StringUtils.isBlank(value)) {
            return;
        }
        String[] valueArray = StringUtils.splitByWholeSeparator(value, commonConstant.SEPARATOR_PIPELINE);
        if (valueArray.length != DidConst.DID_SERVICE_VALUE_MEM_LEN) {
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
        document.setCreated(value);
    }

}
