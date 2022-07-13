package network.platon.pid.sdk.contract.service.impl.processor;

import com.platon.abi.solidity.EventValues;
import com.platon.abi.solidity.datatypes.Event;
import com.platon.crypto.Hash;
import com.platon.protocol.core.DefaultBlockParameterNumber;
import com.platon.protocol.core.methods.response.Log;
import com.platon.protocol.core.methods.response.PlatonBlock;
import com.platon.protocol.core.methods.response.PlatonGetTransactionReceipt;
import com.platon.protocol.core.methods.response.Transaction;
import com.platon.tx.Contract;
import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.enums.ContractStatusEnum;
import network.platon.pid.contract.client.RetryableClient;
import network.platon.pid.contract.dto.CredentialEvidence;
import network.platon.pid.contract.dto.CredentialEvidence.TypeEnum;
import network.platon.pid.sdk.exception.ContractException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
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

//		WasmEvent wasmEvent1 = new WasmEvent(hash, Arrays.asList(new WasmEventParameter(byte[].class, true)), Arrays.asList(new WasmEventParameter(Int8.class) , new WasmEventParameter(String.class) , new WasmEventParameter(Uint64.class) , new WasmEventParameter(Int64.class)));
//		String eventSignature = WasmEventEncoder.encode(wasmEvent1);

		String eventSignature = hash;
		CredentialEvidence typedResponse = new CredentialEvidence();
		try {
			for (Transaction transaction : transList) {
				String transHash = transaction.getHash();
				PlatonGetTransactionReceipt rec1 = RetryableClient.getWeb3j().platonGetTransactionReceipt(transHash).send();
				List<Log> logs = rec1.getResult().getLogs();
				for (Log log : logs) {
					if(log.getTopics() == null || log.getTopics().size() < 2 || !eventSignature.equals( log.getTopics().get(1))){
						continue;
					}
					EventValues eventValues = Contract.staticExtractEventParameters(event, log);;
	                if(eventValues.getNonIndexedValues().size() < 4) {
	                	continue;
	                }

					String typeName = String.valueOf( eventValues.getNonIndexedValues().get(0));
					switch (TypeEnum.findType(typeName)) {
					case SIGNER:
						typedResponse.setSigner(String.valueOf( eventValues.getNonIndexedValues().get(1)));
						break;
					case SIGNATUREDATA:
						typedResponse.setSignaturedata(String.valueOf( eventValues.getNonIndexedValues().get(1)));
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
