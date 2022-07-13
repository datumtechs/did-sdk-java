package network.platon.pid.contract.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InitClientData {

    private String gasLimit;
    
    private String gasPrice;

    private String transPrivateKey;
    
    private String web3Url;
    
    private Long chainId;

    private List<DeployContractData> deployContractDatas;
}
