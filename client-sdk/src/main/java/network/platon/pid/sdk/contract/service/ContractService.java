package network.platon.pid.sdk.contract.service;

import com.platon.crypto.Credentials;
import com.platon.protocol.Web3j;
import com.platon.protocol.Web3jService;
import com.platon.tx.gas.ContractGasProvider;
import com.platon.tx.gas.GasProvider;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.config.PidConfig;
import network.platon.pid.common.enums.Web3jProtocolEnum;
import network.platon.pid.contract.Credential;
import network.platon.pid.contract.Pct;
import network.platon.pid.contract.Pid;
import network.platon.pid.contract.Vote;
import network.platon.pid.contract.client.RetryableClient;
import network.platon.pid.contract.dto.ContractNameValues;
import network.platon.pid.contract.dto.InitContractData;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;

@Slf4j
public abstract class ContractService {

	private static Web3j web3j;
	

	protected static GasProvider gasProvider;

	private Pid pidContract;
	private Pct pctContract;
	private Vote voteContract;
	private Credential credentialContract;
	
	protected static Pid pidStaticContract;
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
		if(StringUtils.isBlank(PidConfig.getPLATON_URL()))
			return;
		RetryableClient retryableClient = new RetryableClient();
		retryableClient.init();
		web3j = retryableClient.getWeb3jWrapper().getWeb3j();
		gasProvider = new ContractGasProvider(new BigInteger(PidConfig.getGAS_PRICE()), new BigInteger(PidConfig.getGAS_LIMIT()));
		Credentials credentials = null;
		if(StringUtils.isNotBlank(PidConfig.getCONTRACT_PRIVATEKEY())) {
			credentials = Credentials.create(PidConfig.getCONTRACT_PRIVATEKEY());
		}
		if(StringUtils.isNotBlank(PidConfig.getPID_CONTRACT_ADDRESS())) {
			pidStaticContract = Pid.load(PidConfig.getPID_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
		if(StringUtils.isNotBlank(PidConfig.getPCT_CONTRACT_ADDRESS())) {
			pctStaticContract = Pct.load(PidConfig.getPCT_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
		if(StringUtils.isNotBlank(PidConfig.getVOTE_CONTRACT_ADDRESS())) {
			voteStaticContract = Vote.load(PidConfig.getVOTE_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
		if(StringUtils.isNotBlank(PidConfig.getCREDENTIAL_CONTRACT_ADDRESS())) {
			credentialStaticContract = Credential.load(PidConfig.getCREDENTIAL_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
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
		if(StringUtils.isNotBlank(PidConfig.getPID_CONTRACT_ADDRESS())
				&&contractNameValues != null && contractNameValues.name().equals(ContractNameValues.PID.name())) {
			pidContract = Pid.load(PidConfig.getPID_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
		if(StringUtils.isNotBlank(PidConfig.getPCT_CONTRACT_ADDRESS())
				&&contractNameValues != null && contractNameValues.name().equals(ContractNameValues.PCT.name())) {
			pctContract = Pct.load(PidConfig.getPCT_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
		if(StringUtils.isNotBlank(PidConfig.getVOTE_CONTRACT_ADDRESS())
				&&contractNameValues != null && contractNameValues.name().equals(ContractNameValues.VOTE.name())) {
			voteContract = Vote.load(PidConfig.getVOTE_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
		if(StringUtils.isNotBlank(PidConfig.getCREDENTIAL_CONTRACT_ADDRESS())
				&&contractNameValues != null && contractNameValues.name().equals(ContractNameValues.CREDENTIAL.name())) {
			credentialContract = Credential.load(PidConfig.getCREDENTIAL_CONTRACT_ADDRESS(), web3j, credentials, gasProvider);
		}
	}

	protected Web3j getWeb3j() {
		return web3j;
	}

	
	protected Pid getPidContract() {
		if(pidContract == null) {
			return ContractService.pidStaticContract;
		}
		return pidContract;
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
