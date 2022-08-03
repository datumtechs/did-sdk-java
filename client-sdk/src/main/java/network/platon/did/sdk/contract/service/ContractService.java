package network.platon.did.sdk.contract.service;

import com.platon.crypto.Credentials;
import com.platon.protocol.Web3j;
import com.platon.protocol.Web3jService;
import com.platon.tx.gas.ContractGasProvider;
import com.platon.tx.gas.GasProvider;
import network.platon.did.contract.Credential;
import network.platon.did.contract.Did;
import network.platon.did.contract.Pct;
import network.platon.did.contract.Vote;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import network.platon.did.common.config.DidConfig;
import network.platon.did.common.enums.Web3jProtocolEnum;
import network.platon.did.contract.client.RetryableClient;
import network.platon.did.contract.dto.ContractNameValues;
import network.platon.did.contract.dto.InitContractData;

import java.math.BigInteger;

@Slf4j
public abstract class ContractService {

	private static Web3j web3j;
	

	protected static GasProvider gasProvider;

	private Did didContract;
	private Pct pctContract;
	private Vote voteContract;
	private Credential credentialContract;
	
	protected static Did didStaticContract;
	protected static Pct pctStaticContract;
	protected static Vote voteStaticContract;
	protected static Credential credentialStaticContract;

	static {
		init();
	}
	
	public static void init() {
		/**
		 * Initialize web3j and corresponding contract address
		 */
		if(StringUtils.isBlank(DidConfig.getPLATON_URL()))
			return;
		RetryableClient retryableClient = new RetryableClient();
		retryableClient.init();
		web3j = retryableClient.getWeb3jWrapper().getWeb3j();
		gasProvider = new ContractGasProvider(new BigInteger(DidConfig.getGAS_PRICE()), new BigInteger(DidConfig.getGAS_LIMIT()));
		Credentials credentials = null;
		if(StringUtils.isNotBlank(DidConfig.getCONTRACT_PRIVATEKEY())) {
			credentials = Credentials.create(DidConfig.getCONTRACT_PRIVATEKEY());
		}
		if(StringUtils.isNotBlank(DidConfig.getDID_CONTRACT_ADDRESS())) {
			didStaticContract = Did.load(DidConfig.getDID_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
		if(StringUtils.isNotBlank(DidConfig.getPCT_CONTRACT_ADDRESS())) {
			pctStaticContract = Pct.load(DidConfig.getPCT_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
		if(StringUtils.isNotBlank(DidConfig.getVOTE_CONTRACT_ADDRESS())) {
			voteStaticContract = Vote.load(DidConfig.getVOTE_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
		if(StringUtils.isNotBlank(DidConfig.getCREDENTIAL_CONTRACT_ADDRESS())) {
			credentialStaticContract = Credential.load(DidConfig.getCREDENTIAL_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
	}
	
	public void reloadAddress(InitContractData initContractData, ContractNameValues contractNameValues) {
		if(initContractData == null) return;
		Credentials credentials = null;
		if(StringUtils.isNotBlank(initContractData.getTransPrivateKey())) {
			credentials = Credentials.create(initContractData.getTransPrivateKey());
		}
		if(StringUtils.isNotBlank(initContractData.getWeb3Url())) {
			Web3jProtocolEnum web3jProtocolEnum = Web3jProtocolEnum.findProtocol(initContractData.getWeb3Url());
			if(web3jProtocolEnum == null) {
				log.error("initContractData.getWeb3Url() is invaild");
				return;
			}
			Web3jService web3jService = RetryableClient.getWeb3Server(web3jProtocolEnum, initContractData.getWeb3Url().substring(web3jProtocolEnum.getHead().length()));
			ContractService.web3j = Web3j.build(web3jService);
		} 
		if(initContractData.getGasProvider() != null) {
			gasProvider = initContractData.getGasProvider();
		}
		initContract(credentials, contractNameValues);
	}
	
	private void initContract(Credentials credentials, ContractNameValues contractNameValues) {
		if(StringUtils.isNotBlank(DidConfig.getDID_CONTRACT_ADDRESS())
				&&contractNameValues != null && contractNameValues.name().equals(ContractNameValues.DID.name())) {
			didContract = Did.load(DidConfig.getDID_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
		if(StringUtils.isNotBlank(DidConfig.getPCT_CONTRACT_ADDRESS())
				&&contractNameValues != null && contractNameValues.name().equals(ContractNameValues.PCT.name())) {
			pctContract = Pct.load(DidConfig.getPCT_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
		if(StringUtils.isNotBlank(DidConfig.getVOTE_CONTRACT_ADDRESS())
				&&contractNameValues != null && contractNameValues.name().equals(ContractNameValues.VOTE.name())) {
			voteContract = Vote.load(DidConfig.getVOTE_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
		if(StringUtils.isNotBlank(DidConfig.getCREDENTIAL_CONTRACT_ADDRESS())
				&&contractNameValues != null && contractNameValues.name().equals(ContractNameValues.CREDENTIAL.name())) {
			credentialContract = Credential.load(DidConfig.getCREDENTIAL_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
	}

	protected Web3j getWeb3j() {
		return web3j;
	}

	
	protected Did getDidContract() {
		if(didContract == null) {
			return ContractService.didStaticContract;
		}
		return didContract;
	}

	protected Pct getPctContract() {
		if(pctContract == null) {
			return ContractService.pctStaticContract;
		}
		return pctContract;
	}

	protected Vote getVoteContract() {
		if(voteContract == null) {
			return ContractService.voteStaticContract;
		}
		return voteContract;
	}

	protected Credential getCredentialContract() {
		if(credentialContract == null) {
			return ContractService.credentialStaticContract;
		}
		return credentialContract;
	}
}
