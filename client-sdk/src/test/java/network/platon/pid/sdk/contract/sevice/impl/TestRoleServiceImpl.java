package network.platon.pid.sdk.contract.sevice.impl;

import org.junit.Test;

import network.platon.pid.sdk.BaseTest;

public class TestRoleServiceImpl extends BaseTest {

    private RoleContractService roleContract = new RoleContractServiceImpl();

    @Test
    public void test_setAuthorityContractAddr() {
    	string string = new string("lax129qreey5gre9wng9nwel0ra2zm5m0uvug7th6q");
        resp = roleContract.setAuthorityContractAddr(string);
    }

    @Test
    public void test_getAuthorityContractAddr() {
        resp = roleContract.getAuthorityContractAddr();
    }
    
}
