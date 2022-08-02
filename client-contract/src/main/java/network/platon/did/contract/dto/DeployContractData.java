package network.platon.did.contract.dto;

import lombok.Data;

@Data
public class DeployContractData {

	private ContractNameValues contractNameValues;
	
	private String contractAddress;
	
	private String txHash;
	
	public DeployContractData(ContractNameValues contractNameValues,String contractAddress,String txHash){
		this.contractNameValues = contractNameValues;
		this.contractAddress = contractAddress;
		this.txHash = txHash;
	}
	
	public DeployContractData(ContractNameValues contractNameValues,String contractAddress){
		this.contractNameValues = contractNameValues;
		this.contractAddress = contractAddress;
	}
	
	public DeployContractData(ContractNameValues contractNameValues){
		this.contractNameValues = contractNameValues;
	}
	
}
