package network.platon.pid.sdk.depoly;


import network.platon.pid.sdk.BaseTest;
import network.platon.pid.sdk.deploy.DeployContract;
import network.platon.pid.sdk.utils.PidUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DepolyContractTest extends BaseTest {

	@Test
	public void test_depolyAllContract() {
		String adminAddress = PidUtils.convertPidToAddressStr(adminPid);
		resp = DeployContract.deployAllContract(adminPrivateKey, adminAddress, adminServiceUrl);
		assertTrue(resp.checkSuccess());
	}

	@Test
	public void test_exportDeployContractData() {
		String adminAddress = PidUtils.convertPidToAddressStr(adminPid);
		DeployContract.exportDeployContractData(adminPrivateKey, adminAddress, adminServiceUrl);
	}
}
