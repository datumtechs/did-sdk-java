package network.platon.pid.sdk.depoly;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import network.platon.pid.sdk.utils.PidUtils;
import org.junit.Test;

import network.platon.pid.common.config.PidConfig;
import network.platon.pid.sdk.BaseTest;
import network.platon.pid.sdk.deploy.DeployContract;

public class DepolyContractTest extends BaseTest {


	@Test
	public void test_depolyAllContract() {
		String adminAddress = PidUtils.convertPidToAddressStr(adminPid);
		resp = DeployContract.deployAllContract(adminPrivateKey, adminAddress);
		assertTrue(resp.checkSuccess());
	}

	@Test
	public void test_exportDeployContractData() {
		String adminAddress = PidUtils.convertPidToAddressStr(adminPid);
		DeployContract.exportDeployContractData(adminPrivateKey, adminAddress);
		System.out.println(PidConfig.getROLE_CONTRACT_ADDRESS());
		assertFalse(PidUtils.generateZeroAddr().equals(PidConfig.getROLE_CONTRACT_ADDRESS()));
	}
}
