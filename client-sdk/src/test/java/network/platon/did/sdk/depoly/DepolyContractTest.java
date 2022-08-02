package network.platon.did.sdk.depoly;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import network.platon.did.sdk.utils.DidUtils;
import org.junit.Test;

import network.platon.did.common.config.DidConfig;
import network.platon.did.sdk.BaseTest;
import network.platon.did.sdk.deploy.DeployContract;

public class DepolyContractTest extends BaseTest {


	@Test
	public void test_depolyAllContract() {
		String adminAddress = DidUtils.convertDidToAddressStr(adminDid);
		resp = DeployContract.deployAllContract(adminPrivateKey, adminAddress, adminServiceUrl);
		assertTrue(resp.checkSuccess());
	}

	@Test
	public void test_exportDeployContractData() {
		String adminAddress = DidUtils.convertDidToAddressStr(adminDid);
		DeployContract.exportDeployContractData(adminPrivateKey, adminAddress, adminServiceUrl);
	}
}
