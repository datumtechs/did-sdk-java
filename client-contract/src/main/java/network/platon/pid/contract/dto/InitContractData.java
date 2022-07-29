package network.platon.pid.contract.dto;

import com.platon.tx.gas.GasProvider;
import lombok.Data;

@Data
public class InitContractData {

    private GasProvider gasProvider;

    private String transPrivateKey;
    
    private String contractAddress;

    private String web3Url;

    public InitContractData(String privateKey) {
        this.transPrivateKey = privateKey;
    }
    
    public InitContractData(String privateKey, String web3Url) {
    	this.transPrivateKey = privateKey;
        this.web3Url = web3Url;
    }
    
    public InitContractData(String privateKey, String web3Url,String contractAddress) {
    	this.transPrivateKey = privateKey;
        this.web3Url = web3Url;
        this.contractAddress = contractAddress;
    }
    
    public InitContractData(String privateKey, GasProvider gasProvider) {
    	this.transPrivateKey = privateKey;
        this.gasProvider = gasProvider;
    }

    public InitContractData(String privateKey, String web3Url,String contractAddress,GasProvider gasProvider) {
    	this.transPrivateKey = privateKey;
        this.web3Url = web3Url;
        this.gasProvider = gasProvider;
        this.contractAddress = contractAddress;
    }

    public void ChangePrivateKey(String privateKey){
        transPrivateKey = privateKey;
    }
}
