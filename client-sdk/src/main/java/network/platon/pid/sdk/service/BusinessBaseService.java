package network.platon.pid.sdk.service;

import network.platon.pid.contract.dto.ContractNameValues;
import network.platon.pid.contract.dto.InitContractData;
import network.platon.pid.sdk.contract.service.VoteContractService;
import network.platon.pid.sdk.contract.service.ContractService;
import network.platon.pid.sdk.contract.service.CredentialContractService;
import network.platon.pid.sdk.contract.service.PctContractService;
import network.platon.pid.sdk.contract.service.PidContractService;
import network.platon.pid.sdk.contract.service.impl.VoteContractServiceImpl;
import network.platon.pid.sdk.contract.service.impl.CredentialContractServiceImpl;
import network.platon.pid.sdk.contract.service.impl.PctContractServiceImpl;
import network.platon.pid.sdk.contract.service.impl.PidContracServiceImpl;
import network.platon.pid.sdk.service.impl.CredentialServiceImpl;
import network.platon.pid.sdk.service.impl.EvidenceServiceImpl;
import network.platon.pid.sdk.service.impl.PctServiceImpl;
import network.platon.pid.sdk.service.impl.PidentityServiceImpl;

public abstract class BusinessBaseService {
	
	private InitContractData initContractData ;

	protected PidentityService getPidentityService(){
		return PidentityServiceImpl.getInstance();
	}

	protected PctService getPctService(){
		return PctServiceImpl.getInstance();
	}
	
	protected CredentialService getCredentialService(){
		return CredentialServiceImpl.getInstance();
	}
	
	protected EvidenceService getEvidenceService(){
		return EvidenceServiceImpl.getInstance();
	}
	
	protected PidContractService getPidContractService(){
		return (PidContractService) this.createContractService(PidContracServiceImpl.getInstance(), ContractNameValues.PID);
	}
	
	protected PctContractService getPctContractService(){
		return (PctContractService) this.createContractService(PctContractServiceImpl.getInstance(), ContractNameValues.PCT);
	}

	protected VoteContractService getVoteContractService(){
		return (VoteContractService) this.createContractService(VoteContractServiceImpl.getInstance(), ContractNameValues.VOTE);
	}

	protected CredentialContractService getCredentialContractService(){
		return (CredentialContractService) this.createContractService(CredentialContractServiceImpl.getInstance(), ContractNameValues.CREDENTIAL);
	}
	
	private ContractService createContractService(ContractService contractService,ContractNameValues contractNameValues) {
		contractService.reloadAddress(initContractData, contractNameValues);
		return contractService;
	}
	
	protected PidContractService getPidContractService(InitContractData initContractData){
		this.initContractData = initContractData;
		return getPidContractService();
	}
	
	protected VoteContractService getVoteContractService(InitContractData initContractData){
		this.initContractData = initContractData;
		return getVoteContractService();
	}

	protected PctContractService getPctContractService(InitContractData initContractData){
		this.initContractData = initContractData;
		return getPctContractService();
	}

	protected CredentialContractService getCredentialContractService(InitContractData initContractData){
		this.initContractData = initContractData;
		return getCredentialContractService();
	}
	
	public void reloadContractData(InitContractData initContractData) {
		this.initContractData = initContractData;
	}
}
