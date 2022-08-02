package network.platon.did.sdk.client;

import network.platon.did.contract.dto.InitContractData;
import network.platon.did.sdk.service.BusinessBaseService;
import network.platon.did.sdk.service.impl.*;

public abstract class BusinessClient {
	
	private InitContractData initContractData ;
	
	protected DidentityServiceImpl getDidentityService (){
		return this.createBaseService(DidentityServiceImpl.getInstance());
	}
	
	protected PctServiceImpl getPctService (){
		return this.createBaseService(PctServiceImpl.getInstance());
	}
	
	protected VoteServiceImpl getVoteService (){
		return this.createBaseService(VoteServiceImpl.getInstance());
	}
	
	protected CredentialServiceImpl getCredentialService (){
		return this.createBaseService(CredentialServiceImpl.getInstance());
	}
	
	protected PresentationServiceImpl getPresentationService (){
		return this.createBaseService(PresentationServiceImpl.getInstance());
	}
	
	protected EvidenceServiceImpl getEvidenceService (){
		return this.createBaseService(EvidenceServiceImpl.getInstance());
	}
	
	private <T> T createBaseService(T data) {
		((BusinessBaseService)data).reloadContractData(initContractData);
		return data;
	}
	
	public InitContractData getInitContractData() {
		return initContractData;
	}

	public void setInitContractData(InitContractData initContractData) {
		this.initContractData = initContractData;
	}
	
}
