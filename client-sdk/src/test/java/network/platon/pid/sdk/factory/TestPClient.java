//package network.platon.pid.sdk.factory;
//
//import org.junit.Test;
//
//import network.platon.pid.contract.dto.InitClientData;
//import network.platon.pid.sdk.BaseTest;
//import network.platon.pid.sdk.client.PidentityClient;
//import network.platon.pid.sdk.client.ReloadClient;
//import network.platon.pid.sdk.deploy.DeployContract;
//import network.platon.pid.sdk.req.pid.CreatePidReq;
//
//public class TestPClient extends BaseTest{
//
//	@Test
//	public void test_pidClient() {
//		InitClientData initClientData = InitClientData.builder()
//				.web3Url("http://10.1.1.9:6789")
//				.gasPrice("10000000000")
//				.gasLimit("4700000")
//				.transPrivateKey(adminPrivateKey)
//				.chainId(200l)
//				.build();
//		ReloadClient.reload(initClientData);
//		String adminAddress = "lax1uqug0zq7rcxddndleq4ux2ft3tv6dqljphydrl";
//		DeployContract.deployContractData(adminPrivateKey, adminAddress);
//		PidentityClient pidentityClient = PClient.createPidentityClient();
//		CreatePidReq req = CreatePidReq.builder()
//				.privateKey(adminPrivateKey)
//				.build();
//		resp = pidentityClient.createPid(req);
//	}
//}
