
package network.platon.did.sdk.factory;

import network.platon.did.common.config.DidConfig;
import network.platon.did.common.enums.Web3jProtocolEnum;
import network.platon.did.contract.dto.InitContractData;
import org.junit.Test;

import network.platon.did.contract.dto.InitClientData;
import network.platon.did.sdk.BaseTest;
import network.platon.did.sdk.client.DidentityClient;
import network.platon.did.sdk.client.ReloadClient;
import network.platon.did.sdk.deploy.DeployContract;
import network.platon.did.sdk.req.did.CreateDidReq;

public class TestPClient extends BaseTest{

	@Test
	public void test_didClient() {
		Web3jProtocolEnum protocol = DidConfig.getWeb3jProtocolEnum();
		String web3Url = "";
		if (protocol == Web3jProtocolEnum.WS){
			web3Url = "http://" + DidConfig.getPLATON_URL();
		}else if (protocol == Web3jProtocolEnum.HTTP){
			web3Url = "ws://" + DidConfig.getPLATON_URL();
		}
		InitClientData initClientData = InitClientData.builder()
				.web3Url(web3Url)
				.gasPrice(DidConfig.getGAS_PRICE())
				.gasLimit(DidConfig.getGAS_LIMIT())
				.transPrivateKey(adminPrivateKey)
				.chainId(DidConfig.getCHAIN_ID())
				.build();
		ReloadClient.reload(initClientData);
		DeployContract.deployContractData(adminPrivateKey, adminAddress, adminServiceUrl);
		DidentityClient didentityClient = PClient.createDidentityClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY()));
		CreateDidReq req = CreateDidReq.builder()
				.privateKey(adminPrivateKey)
				.build();
		resp = didentityClient.createDid(req);
	}
}

