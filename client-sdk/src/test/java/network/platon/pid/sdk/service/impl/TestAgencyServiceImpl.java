package network.platon.pid.sdk.service.impl;

import com.platon.crypto.ECKeyPair;
import com.platon.crypto.Keys;
import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.utils.DateUtils;
import network.platon.pid.sdk.BaseTest;
import network.platon.pid.sdk.base.dto.AuthorityInfo;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.pid.CreatePidResp;
import network.platon.pid.sdk.utils.PidUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import java.math.BigInteger;
import java.util.HashMap;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-18 13:40
 */
@Slf4j
public class TestAgencyServiceImpl extends BaseTest {

    private static final String authorityname = "Gavin authority Issuer";
    private static final String invalidPidPrefixLAT = "did:pid:lat1l8k3ex566hhhjsss5ehpldn6cgsxj4xyrjeerr";
    private static final String invalidPidPrefixLAX = "did:pid:lax1l8k3ex566hhhjsss5ehpldn6cgsxj4xyrjeerr";

    @Test
    public void test_queryAdminRole() {
        okResult(agencyService.queryAdminRole());
    }

    @Test
    public void test_addAuthorityByPid() {


        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            failedResult(createPidResp);
            return;
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "Gavin Issuer" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);
        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        okResult(agencyService.addAuthorityIssuer(addAuthorityReq));
    }

    @Test
    public void test_addAuthorityRepeatPid() {


        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            okResult(createPidResp);
            return;
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "Gavin Issuer repeat1" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        okResult(agencyService.addAuthorityIssuer(addAuthorityReq));

        // repeat add authority by some pid
        authorityname = "Gavin Issuer repeat2" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);
        failedResult(agencyService.addAuthorityIssuer(addAuthorityReq));

    }


    @Test
    public void test_addAuthorityRepeatName() {


        BaseResp<CreatePidResp> createPidResp1 = this.createPidBase();
        if (createPidResp1.checkFail()) {
            okResult(createPidResp1);
            return;
        }
        String pid1 = createPidResp1.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid1);

        String authorityname = "Repeat Name Issuer" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        okResult(agencyService.addAuthorityIssuer(addAuthorityReq));

        // repeat add authority by some name
        BaseResp<CreatePidResp> createPidResp2 = this.createPidBase();
        if (createPidResp2.checkFail()) {
            okResult(createPidResp2);
            return;
        }
        String pid2 = createPidResp2.getData().getPid();
        authorityInfo.setPid(pid2);

        addAuthorityReq.setAuthority(authorityInfo);

        failedResult(agencyService.addAuthorityIssuer(addAuthorityReq));

    }



    @Test
    public void test_addAuthorityByInvalidPid() {


        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            failedResult(createPidResp);
            return;
        }
        String pid = createPidResp.getData().getPid();

        // phrase 1:

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(invalidPidPrefixLAT);

        String authorityname = "InvalidPid Add Issuer" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        failedResult(agencyService.addAuthorityIssuer(addAuthorityReq));

        // phrase 2:
        authorityInfo.setPid(invalidPidPrefixLAX);
        addAuthorityReq.setAuthority(authorityInfo);
        failedResult(agencyService.addAuthorityIssuer(addAuthorityReq));
    }


    @Test
    public void test_addAuthorityWithoutAdmin() {


        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            failedResult(createPidResp);
            return;
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "Without Admin" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(createPidResp.getData().getPrivateKey())
                .authority(authorityInfo)
                .build();

        failedResult(agencyService.addAuthorityIssuer(addAuthorityReq));
    }

    @Test
    public void test_addAuthorityWithLongName() {


        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            failedResult(createPidResp);
            return;
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "With Long Name Issuer XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        failedResult(agencyService.addAuthorityIssuer(addAuthorityReq));
    }


    @Test
    public void test_isAuthorityIssuer() {

        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            okResult(createPidResp);
            return;
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "isAuthority" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        BaseResp<SetAuthorityResp> addAuthorityResp = agencyService.addAuthorityIssuer(addAuthorityReq);
        if (addAuthorityResp.checkFail()) {
            okResult(addAuthorityResp);
            return;
        }

        // whether the pid is an authority ?
        okResult(agencyService.isAuthorityIssuer(pid));
    }


    @Test
    public void test_isAuthorityIssuerWithInvalidPid() {

        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            okResult(createPidResp);
            return;
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "isAuth Invalid" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        BaseResp<SetAuthorityResp> addAuthorityResp = agencyService.addAuthorityIssuer(addAuthorityReq);
        if (addAuthorityResp.checkFail()) {
            okResult(addAuthorityResp);
            return;
        }

        // whether the invalid pid is an authority ?
        failedResult(agencyService.isAuthorityIssuer(invalidPidPrefixLAT));
        failedResult(agencyService.isAuthorityIssuer(invalidPidPrefixLAX));

        ECKeyPair keyPair = null;
        try {
            keyPair = Keys.createEcKeyPair();
        } catch (Exception e) {
            log.error("Failed to create EcKeyPair, exception: {}", e);
            return;
        }
        String randomPid = PidUtils.generatePid(Numeric.toHexStringWithPrefix(keyPair.getPrivateKey()));
        failedResult(agencyService.isAuthorityIssuer(randomPid));
    }



    @Test
    public void test_updateAuthorityIssuer() {

        // add a nwe authority issuer
        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            okResult(createPidResp);
            return;
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "update Issuer" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        BaseResp<SetAuthorityResp> addAuthorityResp = agencyService.addAuthorityIssuer(addAuthorityReq);
        if (addAuthorityResp.checkFail()) {
            okResult(addAuthorityResp);
            return;
        }


        // update authority
        authorityInfo.setPid(pid);
        String newName = "Xujiacan Issuer" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(newName);

        SetAuthorityReq updateAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();
        okResult(agencyService.updateAuthorityIssuer(updateAuthorityReq));
    }


    @Test
    public void test_updateAuthorityIssuerRepeatName() {

        // add a nwe authority issuer 1
        BaseResp<CreatePidResp> createPidResp1 = this.createPidBase();
        if (createPidResp1.checkFail()) {
            okResult(createPidResp1);
            return;
        }
        String pid1 = createPidResp1.getData().getPid();

        AuthorityInfo authorityInfo1 = new AuthorityInfo();
        authorityInfo1.setPid(pid1);

        String authorityname1 = "update Issuer 1" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo1.setName(authorityname1);

        authorityInfo1.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo1.setAccumulate(BigInteger.valueOf(0));
        authorityInfo1.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq1 = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo1)
                .build();

        BaseResp<SetAuthorityResp> addAuthorityResp1 = agencyService.addAuthorityIssuer(addAuthorityReq1);
        if (addAuthorityResp1.checkFail()) {
            okResult(addAuthorityResp1);
            return;
        }


        // add a nwe authority issuer 2
        BaseResp<CreatePidResp> createPidResp2 = this.createPidBase();
        if (createPidResp2.checkFail()) {
            okResult(createPidResp2);
            return;
        }
        String pid2 = createPidResp2.getData().getPid();

        AuthorityInfo authorityInfo2 = new AuthorityInfo();
        authorityInfo2.setPid(pid2);

        String authorityname2 = "update Issuer 2" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo2.setName(authorityname2);

        authorityInfo2.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo2.setAccumulate(BigInteger.valueOf(0));
        authorityInfo2.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq2 = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo2)
                .build();

        BaseResp<SetAuthorityResp> addAuthorityResp2 = agencyService.addAuthorityIssuer(addAuthorityReq2);
        if (addAuthorityResp2.checkFail()) {
            okResult(addAuthorityResp2);
            return;
        }


        // update authority1
        authorityInfo1.setPid(pid1);
        authorityInfo1.setName(authorityname2);

        SetAuthorityReq updateAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo1)
                .build();
        failedResult(agencyService.updateAuthorityIssuer(updateAuthorityReq));
    }

    @Test
    public void test_updateAuthorityIssuerWithInvalidPid() {

        // add a nwe authority issuer
        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            okResult(createPidResp);
            return;
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "invalid up Issuer" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        BaseResp<SetAuthorityResp> addAuthorityResp = agencyService.addAuthorityIssuer(addAuthorityReq);
        if (addAuthorityResp.checkFail()) {
            okResult(addAuthorityResp);
            return;
        }





        // update authority by  invalid lat pid
        authorityInfo.setPid(invalidPidPrefixLAT);
        String newName = "Xujiacan Issuer" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(newName);

        SetAuthorityReq updateAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();
       failedResult(agencyService.updateAuthorityIssuer(updateAuthorityReq));


        // update authority by  invalid lax pid
        authorityInfo.setPid(invalidPidPrefixLAT);
        authorityInfo.setName(newName);

        updateAuthorityReq.setAuthority(authorityInfo);
        failedResult(agencyService.updateAuthorityIssuer(updateAuthorityReq));

        // update authority by  invalid random pid
        ECKeyPair keyPair = null;
        try {
            keyPair = Keys.createEcKeyPair();
        } catch (Exception e) {
            log.error("Failed to create EcKeyPair, exception: {}", e);
            return;
        }
        String randomPid = PidUtils.generatePid(Numeric.toHexStringWithPrefix(keyPair.getPrivateKey()));
        authorityInfo.setPid(randomPid);
        authorityInfo.setName(newName);

        updateAuthorityReq.setAuthority(authorityInfo);
        failedResult(agencyService.updateAuthorityIssuer(updateAuthorityReq));

    }


    @Test
    public void test_removeAuthorityIssuer() {
        // add a nwe authority issuer
        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            okResult(createPidResp);
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "remove Issuer" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        BaseResp<SetAuthorityResp> addAuthorityResp = agencyService.addAuthorityIssuer(addAuthorityReq);
        if (addAuthorityResp.checkFail()) {
            okResult(addAuthorityResp);
        }

        // remove authority
        RevocationAuthorityReq removeAuthorityReq = RevocationAuthorityReq.builder()
                .pid(pid)
                .privateKey(adminPrivateKey)
                .build();
        okResult(agencyService.removeAuthorityIssuer(removeAuthorityReq));
    }


    @Test
    public void test_removeAuthorityIssuerWithInvalidPid() {
        // add a nwe authority issuer
        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            okResult(createPidResp);
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "Invalid rm Issuer" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        BaseResp<SetAuthorityResp> addAuthorityResp = agencyService.addAuthorityIssuer(addAuthorityReq);
        if (addAuthorityResp.checkFail()) {
            okResult(addAuthorityResp);
        }


        // remove authority by  invalid random pid
        ECKeyPair keyPair = null;
        try {
            keyPair = Keys.createEcKeyPair();
        } catch (Exception e) {
            log.error("Failed to create EcKeyPair, exception: {}", e);
            return;
        }
        String randomPid = PidUtils.generatePid(Numeric.toHexStringWithPrefix(keyPair.getPrivateKey()));

        // remove authority
        RevocationAuthorityReq removeAuthorityReq = RevocationAuthorityReq.builder()
                .pid(randomPid)
                .privateKey(adminPrivateKey)
                .build();
        failedResult(agencyService.removeAuthorityIssuer(removeAuthorityReq));
    }


    @Test
    public void test_getAuthorityIssuerByAddr() {
        // add a nwe authority issuer
        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            failedResult(createPidResp);
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "Gavin ByAddr" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        BaseResp<SetAuthorityResp> addAuthorityResp = agencyService.addAuthorityIssuer(addAuthorityReq);
        if (addAuthorityResp.checkFail()) {
            okResult(addAuthorityResp);
        }

        // query the authority info by pid
        okResult(agencyService.getAuthorityIssuerByPid(pid));
    }


    @Test
    public void test_getAuthorityIssuerByInvalidAddr() {

        ECKeyPair keyPair = null;
        try {
            keyPair = Keys.createEcKeyPair();
        } catch (Exception e) {
            log.error("Failed to create EcKeyPair, exception: {}", e);
            return;
        }
        String randomPid = PidUtils.generatePid(Numeric.toHexStringWithPrefix(keyPair.getPrivateKey()));
        // query the authority info by random pid
        failedResult(agencyService.getAuthorityIssuerByPid(randomPid));
    }



    @Test
    public void test_getAuthorityIssuerByName() {
        // add a nwe authority issuer
        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            failedResult(createPidResp);
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "Gavin ByName" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        BaseResp<SetAuthorityResp> addAuthorityResp = agencyService.addAuthorityIssuer(addAuthorityReq);
        if (addAuthorityResp.checkFail()) {
            okResult(addAuthorityResp);
        }

        // query the authority info by name
        okResult(agencyService.getAuthorityIssuerByName(authorityname));
    }

    @Test
    public void test_getAuthorityIssuerByNameNoExist() {
        // add a nwe authority issuer
        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            failedResult(createPidResp);
            return;
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "NameNoExist" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);

        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        BaseResp<SetAuthorityResp> addAuthorityResp = agencyService.addAuthorityIssuer(addAuthorityReq);
        if (addAuthorityResp.checkFail()) {
            okResult(addAuthorityResp);
            return;
        }
        // query authority info by invalid name
        failedResult(agencyService.getAuthorityIssuerByName("authorityname"));
    }


    @Test
    public void test_getAccumulateOfAuthorityIssuer() {
        // add a nwe authority issuer
        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            failedResult(createPidResp);
            return;
        }
        String pid = createPidResp.getData().getPid();

        AuthorityInfo authorityInfo = new AuthorityInfo();
        authorityInfo.setPid(pid);

        String authorityname = "Accumulate" + RandomStringUtils.randomAlphanumeric(10);
        authorityInfo.setName(authorityname);
        authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
        authorityInfo.setAccumulate(BigInteger.valueOf(0));
        authorityInfo.setExtra(new HashMap<String, Object>());

        SetAuthorityReq addAuthorityReq = SetAuthorityReq.builder()
                .privateKey(adminPrivateKey)
                .authority(authorityInfo)
                .build();

        BaseResp<SetAuthorityResp> addAuthorityResp = agencyService.addAuthorityIssuer(addAuthorityReq);
        if (addAuthorityResp.checkFail()) {
            okResult(addAuthorityResp);
            return;
        }

        // query the accumulate by pid
        okResult(agencyService.getAccumulateOfAuthorityIssuer(pid));
    }



    @Test
    public void test_getAccumulateOfAuthorityIssuerByInvalidPid() {
        ECKeyPair keyPair = null;
        try {
            keyPair = Keys.createEcKeyPair();
        } catch (Exception e) {
            log.error("Failed to create EcKeyPair, exception: {}", e);
            return;
        }
        String randomPid = PidUtils.generatePid(Numeric.toHexStringWithPrefix(keyPair.getPrivateKey()));

        // query the accumulate by random pid
        failedResult(agencyService.getAccumulateOfAuthorityIssuer(randomPid));
    }


    @Test
    public void test_getAllAuthorityIssuerNameList() {
        // add a nwe authority issuer
        this.test_addAuthorityByPid();

        // query all names of authority
        okResult(agencyService.getAllAuthorityIssuerNameList());
    }


}
