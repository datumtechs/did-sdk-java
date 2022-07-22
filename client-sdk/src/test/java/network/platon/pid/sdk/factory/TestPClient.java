package network.platon.pid.sdk.factory;

import network.platon.pid.common.config.PidConfig;
import network.platon.pid.common.enums.Web3jProtocolEnum;
import org.junit.Test;

import network.platon.pid.contract.dto.InitClientData;
import network.platon.pid.sdk.BaseTest;
import network.platon.pid.sdk.client.PidentityClient;
import network.platon.pid.sdk.client.ReloadClient;
import network.platon.pid.sdk.deploy.DeployContract;
import network.platon.pid.sdk.req.pid.CreatePidReq;

public class TestPClient extends BaseTest{

	@Test
	public void test_pidClient() {
		Web3jProtocolEnum protocol = PidConfig.getWeb3jProtocolEnum();
		String web3Url = "";
		if (protocol == Web3jProtocolEnum.WS){
			web3Url = "http://" + PidConfig.getPLATON_URL();
		}else if (protocol == Web3jProtocolEnum.HTTP){
			web3Url = "ws://" + PidConfig.getPLATON_URL();
		}
		InitClientData initClientData = InitClientData.builder()
				.web3Url(web3Url)
				.gasPrice(PidConfig.getGAS_PRICE())
				.gasLimit(PidConfig.getGAS_LIMIT())
				.transPrivateKey(adminPrivateKey)
				.chainId(PidConfig.getCHAIN_ID())
				.build();
		ReloadClient.reload(initClientData);
		DeployContract.deployContractData(adminPrivateKey, adminAddress, adminServiceUrl);
		PidentityClient pidentityClient = PClient.createPidentityClient();
		CreatePidReq req = CreatePidReq.builder()
				.privateKey(adminPrivateKey)
				.build();
		resp = pidentityClient.createPid(req);
	}
}
