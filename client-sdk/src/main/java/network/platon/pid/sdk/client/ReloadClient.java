package network.platon.pid.sdk.client;

import network.platon.pid.common.config.PidConfig;
import network.platon.pid.common.enums.Web3jProtocolEnum;
import network.platon.pid.contract.dto.DeployContractData;
import network.platon.pid.contract.dto.InitClientData;
import network.platon.pid.sdk.contract.service.ContractService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ReloadClient {

	public static void reload(InitClientData initClientData) {
		if(initClientData.getChainId() != null) {
			PidConfig.setCHAIN_ID(initClientData.getChainId());
		}
		if(StringUtils.isNotBlank(initClientData.getTransPrivateKey())) {
			PidConfig.setCONTRACT_PRIVATEKEY(initClientData.getTransPrivateKey());
		}
		if(StringUtils.isNotBlank(initClientData.getGasLimit())) {
			PidConfig.setGAS_LIMIT(initClientData.getGasLimit());
		}
		if(StringUtils.isNotBlank(initClientData.getGasPrice())) {
			PidConfig.setGAS_PRICE(initClientData.getGasPrice());
		}
		if(StringUtils.isNotBlank(initClientData.getWeb3Url())) {
			Web3jProtocolEnum web3jProtocolEnum = Web3jProtocolEnum.findProtocol(initClientData.getWeb3Url());
			PidConfig.setWeb3jProtocolEnum(web3jProtocolEnum);
			PidConfig.setPLATON_URL(initClientData.getWeb3Url().substring(web3jProtocolEnum.getHead().length()));
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
			case PID:
				PidConfig.setPID_CONTRACT_ADDRESS(deployContractData.getContractAddress());
				break;
			case PCT:
				PidConfig.setPCT_CONTRACT_ADDRESS(deployContractData.getContractAddress());
				break;
			case VOTE:
				PidConfig.setVOTE_CONTRACT_ADDRESS(deployContractData.getContractAddress());
				break;
			case CREDENTIAL:
				PidConfig.setCREDENTIAL_CONTRACT_ADDRESS(deployContractData.getContractAddress());
				break;
			default:
				break;
			}
		}
	}
}
