package network.platon.did.sdk.contract.service.impl.processor;

import com.platon.abi.solidity.EventEncoder;
import com.platon.abi.solidity.EventValues;
import com.platon.abi.solidity.datatypes.Event;

import com.platon.protocol.core.DefaultBlockParameterNumber;
import com.platon.protocol.core.methods.response.*;
import com.platon.tx.Contract;
import lombok.extern.slf4j.Slf4j;

import network.platon.did.common.enums.ContractStatusEnum;
import network.platon.did.contract.Credential;
import network.platon.did.contract.client.RetryableClient;
import network.platon.did.contract.dto.CredentialEvidence;
import network.platon.did.contract.dto.CredentialEvidence.TypeEnum;
import network.platon.did.sdk.exception.ContractException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class CredentialEventProcessor {

	
	public static CredentialEvidence decodeCredentialByBlock(BigInteger currentBlockNumber, Event event, String hash) {
		PlatonBlock latestBlock = null;
		try {
			latestBlock = RetryableClient.getWeb3j()
					.platonGetBlockByNumber(new DefaultBlockParameterNumber(currentBlockNumber), true).send();
		} catch (IOException e) {
			log.error("platonGetBlockByNumber error,  currentBlockNumber:{} ", currentBlockNumber, e);
			throw new ContractException(ContractStatusEnum.C_NETWOEK_EXCEPTION);
		}
		if (latestBlock == null) {
			log.info("platonGetBlockByNumber latestBlock is null,  currentBlockNumber:{} ", currentBlockNumber);
			throw new ContractException(ContractStatusEnum.C_NETWOEK_EXCEPTION);
		}
		List<Transaction> transList = latestBlock.getBlock().getTransactions().stream()
				.map(transactionResult -> (Transaction) transactionResult.get()).collect(Collectors.toList());


		String evidenceHash = hash;
		String creadentialChangeTopic = EventEncoder.encode(Credential.CREDENTIALATTRIBUTECHANGE_EVENT);
		CredentialEvidence typedResponse = new CredentialEvidence();
		try {
			for (Transaction transaction : transList) {
				String transHash = transaction.getHash();
				PlatonGetTransactionReceipt rec1 = RetryableClient.getWeb3j().platonGetTransactionReceipt(transHash).send();
				List<Log> logs = rec1.getResult().getLogs();
				for (Log log : logs) {
					if(log.getTopics() == null || log.getTopics().size() < 2
							|| ! creadentialChangeTopic.equals(log.getTopics().get(0)) || !evidenceHash.equals( log.getTopics().get(1))){
						continue;
					}
					EventValues eventValues = Contract.staticExtractEventParameters(event, log);;
	                if(eventValues.getNonIndexedValues().size() < 4) {
	                	continue;
	                }

					String typeName = String.valueOf((BigInteger) eventValues.getNonIndexedValues().get(0).getValue());
					switch (TypeEnum.findType(typeName)) {
					case SIGNER:
						typedResponse.setSigner((String) eventValues.getNonIndexedValues().get(1).getValue());
						typedResponse.setCreate((String) eventValues.getNonIndexedValues().get(3).getValue());
						break;
					case SIGNATUREDATA:
						typedResponse.setSignaturedata((String) eventValues.getNonIndexedValues().get(1).getValue());
						typedResponse.setCreate((String) eventValues.getNonIndexedValues().get(3).getValue());
						break;
					default:
						break;
					}
				}
			}
			return typedResponse;
		} catch (IOException e) {
			log.error("get TransactionReceipt failed.", e);
			throw new ContractException(ContractStatusEnum.C_NETWOEK_EXCEPTION);
		}
	}
}
