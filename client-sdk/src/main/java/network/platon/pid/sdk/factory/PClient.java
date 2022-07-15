package network.platon.pid.sdk.factory;

import network.platon.pid.contract.dto.InitContractData;
import network.platon.pid.sdk.client.VoteClient;
import network.platon.pid.sdk.client.BusinessClient;
import network.platon.pid.sdk.client.CredentialClient;
import network.platon.pid.sdk.client.EvidenceClient;
import network.platon.pid.sdk.client.PctClient;
import network.platon.pid.sdk.client.PidentityClient;
import network.platon.pid.sdk.client.PresentationClient;

public class PClient{

	private static BusinessClient createClient(InitContractData initContractData, BusinessClient data) {
		data.setInitContractData(initContractData);
		return data;
	}
	
	public static PidentityClient createPidentityClient(InitContractData initContractData) {
		return (PidentityClient) createClient(initContractData,new PidentityClient());
	}
	
	public static PidentityClient createPidentityClient() {
		return new PidentityClient();
	}

	public static PctClient createPctClient(InitContractData initContractData) {
		return (PctClient) createClient(initContractData,new PctClient());
	}
	
	public static PctClient createPctClient() {
		return new PctClient();
	}

	public static VoteClient createAgencyClient(InitContractData initContractData) {
		return (VoteClient) createClient(initContractData,new VoteClient());
	}
	
	public static VoteClient createAgencyClient() {
		return new VoteClient();
	}

	public static CredentialClient createCredentialClient(InitContractData initContractData) {
		return (CredentialClient) createClient(initContractData,new CredentialClient());
	}
	
	public static CredentialClient createCredentialClient() {
		return new CredentialClient();
	}

	public static PresentationClient createPresentationClient(InitContractData initContractData) {
		return (PresentationClient) createClient(initContractData,new PresentationClient());
	}
	
	public static PresentationClient createPresentationClient() {
		return new PresentationClient();
	}

	public static EvidenceClient createEvidenceClient(InitContractData initContractData) {
		return (EvidenceClient) createClient(initContractData,new EvidenceClient());
	}
	
	public static EvidenceClient createEvidenceClient() {
		return new EvidenceClient();
	}

}
