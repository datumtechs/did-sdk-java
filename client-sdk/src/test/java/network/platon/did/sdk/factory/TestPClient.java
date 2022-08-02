//package network.platon.did.sdk.factory;
//
//import org.junit.Test;
//
//import InitClientData;
//import network.platon.did.sdk.BaseTest;
//import network.platon.did.sdk.client.DidentityClient;
//import network.platon.did.sdk.client.ReloadClient;
//import network.platon.did.sdk.deploy.DeployContract;
//import network.platon.did.sdk.req.did.CreateDidReq;
//
//public class TestPClient extends BaseTest{
//
//	@Test
//	public void test_didClient() {
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
//		DidentityClient didentityClient = PClient.createDidentityClient();
//		CreateDidReq req = CreateDidReq.builder()
//				.privateKey(adminPrivateKey)
//				.build();
//		resp = didentityClient.createDid(req);
//	}
//}
