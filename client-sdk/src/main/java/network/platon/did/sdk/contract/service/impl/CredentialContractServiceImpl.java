package network.platon.did.sdk.contract.service.impl;

import com.platon.crypto.Credentials;
import com.platon.protocol.core.methods.response.TransactionReceipt;
import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.contract.Credential;
import network.platon.did.contract.dto.ContractNameValues;
import network.platon.did.contract.dto.CredentialEvidence;
import network.platon.did.contract.dto.DeployContractData;
import network.platon.did.contract.dto.TransactionInfo;
import network.platon.did.sdk.contract.service.ContractService;
import network.platon.did.sdk.contract.service.CredentialContractService;
import network.platon.did.sdk.contract.service.impl.processor.CredentialEventProcessor;
import network.platon.did.sdk.resp.TransactionResp;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class CredentialContractServiceImpl extends ContractService implements CredentialContractService,Serializable,Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6963401261601796153L;

	private static CredentialContractServiceImpl credentialContractServiceImpl = new CredentialContractServiceImpl();
	
	public static CredentialContractServiceImpl getInstance(){
        try {
            return (CredentialContractServiceImpl) credentialContractServiceImpl.clone();
        } catch (CloneNotSupportedException e) {
        	log.error("get instance error.", e);
        }
        return new CredentialContractServiceImpl();
    }
	
	public TransactionResp<List<DeployContractData>> deployContract(Credentials credentials, String voteContractAddress) {
		try {
			Credential credentialContract = Credential.deploy(getWeb3j(), credentials, gasProvider).send();
			TransactionReceipt receipt = credentialContract.initialize(voteContractAddress).send();
			if(!receipt.isStatusOK()){
				log.error("deployContract CredentialContract error");
				return TransactionResp.build(RetEnum.RET_DEPLOY_CONTRACT_ERROR, "deployContract CredentialContract error");
			}
			String contractAddress = credentialContract.getContractAddress();
			String transHash = "";
			Optional<TransactionReceipt> value = credentialContract.getTransactionReceipt();
			if(value.isPresent()){
				transHash = value.get().getTransactionHash();
			}
			DeployContractData depolyContractData = new DeployContractData(ContractNameValues.CREDENTIAL
					,contractAddress,transHash);
			List<DeployContractData> lists = Arrays.asList(depolyContractData);
			return TransactionResp.buildSuccess(lists);
		} catch (Exception e) {
			log.error("deployContract CredentialContract error", e);
			return TransactionResp.build(RetEnum.RET_DEPLOY_CONTRACT_ERROR, e.getMessage());
		}
	}

	@Override
	public TransactionResp<String> createCredentialEvience(String hash, String signer, String signatureData, String updateTime) {
		try {
			TransactionReceipt transactionReceipt = this.getCredentialContract().createCredential(Numeric.hexStringToByteArray(hash), signer, signatureData, updateTime).send();
			return TransactionResp.buildTxSuccess(new TransactionInfo(transactionReceipt));
		} catch (Exception e) {
			log.error("create credential erorr.", e);
			return TransactionResp.build(RetEnum.RET_CREDENTIAL_CONTRACT_CREATE_ERROR);
		}
	}

	@Override
	public TransactionResp<CredentialEvidence> queryCredentialEvience(String hash) {
		try {
			BigInteger blockNumber = this.getCredentialContract().getLatestBlock(Numeric.hexStringToByteArray(hash)).send();
			if(blockNumber == null || blockNumber.longValue() == 0) {
				return TransactionResp.build(RetEnum.RET_CREDENTIAL_CONTRACT_NOT_FOUND_ERROR);
			}
			CredentialEvidence credentialEvidence = CredentialEventProcessor.decodeCredentialByBlock(blockNumber, Credential.CREDENTIALATTRIBUTECHANGE_EVENT, hash);
			if(credentialEvidence == null) {
				return TransactionResp.build(RetEnum.RET_CREDENTIAL_CONTRACT_NOT_FOUND_ERROR);
			}
			return TransactionResp.buildSuccess(credentialEvidence);
		} catch (Exception e) {
			log.error("query credential erorr.", e);
			return TransactionResp.build(RetEnum.RET_CREDENTIAL_CONTRACT_QUERTY_ERROR);
		}
	}

	@Override
	public TransactionResp<Boolean> isHashExit(String hash) {
		try {
			Boolean flag = this.getCredentialContract().isHashExist(Numeric.hexStringToByteArray(hash)).send();
			return TransactionResp.buildSuccess(flag);
		} catch (Exception e) {
			log.error("query credential erorr.", e);
			return TransactionResp.build(RetEnum.RET_CREDENTIAL_CONTRACT_QUERTY_ERROR);
		}
	}

	@Override
	public TransactionResp<BigInteger> getStatus(String hash) {
		try {
			BigInteger status = this.getCredentialContract().getStatus(Numeric.hexStringToByteArray(hash)).send();
			return TransactionResp.buildSuccess(status);
		} catch (Exception e) {
			log.error("query credential status erorr.", e);
			return TransactionResp.build(RetEnum.RET_CREDENTIAL_GET_STATUS_FAIL);
		}
	}

	@Override
	public TransactionResp<Boolean> changeStatus(String hash,BigInteger status) {
		try {
			TransactionReceipt transactionReceipt = this.getCredentialContract().changeStatus(hash.getBytes(), status).send();
			return TransactionResp.buildTxSuccess(new TransactionInfo(transactionReceipt));
		} catch (Exception e) {
			log.error("change credential status erorr.", e);
			return TransactionResp.build(RetEnum.RET_CREDENTIAL_CONTRACT_QUERTY_ERROR);
		}
	}
}
