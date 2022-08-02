package network.platon.did.sdk.client;

import network.platon.did.common.config.DidConfig;
import network.platon.did.common.enums.Web3jProtocolEnum;
import network.platon.did.contract.dto.DeployContractData;
import network.platon.did.contract.dto.InitClientData;
import network.platon.did.sdk.contract.service.ContractService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ReloadClient {

	public static void reload(InitClientData initClientData) {
		if(initClientData.getChainId() != null) {
			DidConfig.setCHAIN_ID(initClientData.getChainId());
		}
		if(StringUtils.isNotBlank(initClientData.getTransPrivateKey())) {
			DidConfig.setCONTRACT_PRIVATEKEY(initClientData.getTransPrivateKey());
		}
		if(StringUtils.isNotBlank(initClientData.getGasLimit())) {
			DidConfig.setGAS_LIMIT(initClientData.getGasLimit());
		}
		if(StringUtils.isNotBlank(initClientData.getGasPrice())) {
			DidConfig.setGAS_PRICE(initClientData.getGasPrice());
		}
		if(StringUtils.isNotBlank(initClientData.getWeb3Url())) {
			Web3jProtocolEnum web3jProtocolEnum = Web3jProtocolEnum.findProtocol(initClientData.getWeb3Url());
			DidConfig.setWeb3jProtocolEnum(web3jProtocolEnum);
			DidConfig.setPLATON_URL(initClientData.getWeb3Url().substring(web3jProtocolEnum.getHead().length()));
		}
		
		List<DeployContractData> deployContractDatas = initClientData.getDeployContractDatas();
		if(deployContractDatas != null) {
			deployContractData(deployContractDatas);
		}

		ContractService.init();
	}
	
	public static void deployContractData(List<DeployContractData> deployContractDatas) {
		for (DeployContractData deployContractData : deployContractDatas) {
			switch (deployContractData.getContractNameValues()) {
			case DID:
				DidConfig.setDID_CONTRACT_ADDRESS(deployContractData.getContractAddress());
				break;
			case PCT:
				DidConfig.setPCT_CONTRACT_ADDRESS(deployContractData.getContractAddress());
				break;
			case VOTE:
				DidConfig.setVOTE_CONTRACT_ADDRESS(deployContractData.getContractAddress());
				break;
			case CREDENTIAL:
				DidConfig.setCREDENTIAL_CONTRACT_ADDRESS(deployContractData.getContractAddress());
				break;
			default:
				break;
			}
		}
	}
}
