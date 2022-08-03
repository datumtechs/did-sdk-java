package network.platon.did.sdk.service;

import network.platon.did.contract.dto.ContractNameValues;
import network.platon.did.contract.dto.InitContractData;
import network.platon.did.sdk.contract.service.VoteContractService;
import network.platon.did.sdk.contract.service.ContractService;
import network.platon.did.sdk.contract.service.CredentialContractService;
import network.platon.did.sdk.contract.service.PctContractService;
import network.platon.did.sdk.contract.service.DidContractService;
import network.platon.did.sdk.contract.service.impl.VoteContractServiceImpl;
import network.platon.did.sdk.contract.service.impl.CredentialContractServiceImpl;
import network.platon.did.sdk.contract.service.impl.PctContractServiceImpl;
import network.platon.did.sdk.contract.service.impl.DidContracServiceImpl;
import network.platon.did.sdk.service.impl.CredentialServiceImpl;
import network.platon.did.sdk.service.impl.EvidenceServiceImpl;
import network.platon.did.sdk.service.impl.PctServiceImpl;
import network.platon.did.sdk.service.impl.DidentityServiceImpl;

public abstract class BusinessBaseService {

    private InitContractData initContractData;

    protected DidentityService getDidentityService() {
        return DidentityServiceImpl.getInstance();
    }

    protected PctService getPctService() {
        return PctServiceImpl.getInstance();
    }

    private ContractService createContractService(ContractService contractService,ContractNameValues contractNameValues) {
		contractService.reloadAddress(initContractData, contractNameValues);
		return contractService;
	}

    protected CredentialService getCredentialService() {
        return CredentialServiceImpl.getInstance();
    }

    protected EvidenceService getEvidenceService() {
        return EvidenceServiceImpl.getInstance();
    }

    protected DidContractService getDidContractService() {
        return (DidContractService) this.createContractService(DidContracServiceImpl.getInstance(), ContractNameValues.DID);
    }

    protected PctContractService getPctContractService() {
        return (PctContractService) this.createContractService(PctContractServiceImpl.getInstance(), ContractNameValues.PCT);
    }

    protected VoteContractService getVoteContractService() {
        return (VoteContractService) this.createContractService(VoteContractServiceImpl.getInstance(), ContractNameValues.VOTE);
    }

    protected CredentialContractService getCredentialContractService() {
        return (CredentialContractService) this.createContractService(CredentialContractServiceImpl.getInstance(), ContractNameValues.CREDENTIAL);
    }

    protected DidContractService getDidContractService(InitContractData initContractData) {
        this.initContractData = initContractData;
        return getDidContractService();
    }

    protected VoteContractService getVoteContractService(InitContractData initContractData) {
        this.initContractData = initContractData;
        return getVoteContractService();
    }

    protected PctContractService getPctContractService(InitContractData initContractData) {
        this.initContractData = initContractData;
        return getPctContractService();
    }

    protected CredentialContractService getCredentialContractService(InitContractData initContractData) {
        this.initContractData = initContractData;
        return getCredentialContractService();
    }

    protected void ChangePrivateKey(String privateKey) {
        this.initContractData.ChangePrivateKey(privateKey);
    }

    public void reloadContractData(InitContractData initContractData) {
        this.initContractData = initContractData;
    }
}
