package network.platon.pid.sdk;

import com.alibaba.fastjson.JSONObject;
import com.platon.bech32.Bech32;
import com.platon.crypto.Credentials;
import com.platon.crypto.ECKeyPair;
import com.platon.crypto.Keys;
import com.platon.parameters.NetworkParameters;
import com.platon.protocol.Web3j;
import com.platon.protocol.core.methods.response.TransactionReceipt;
import com.platon.tx.Transfer;
import com.platon.utils.Convert;
import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.config.PidConfig;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.common.utils.DateUtils;
import network.platon.pid.common.utils.PropertyUtils;
import network.platon.pid.contract.client.RetryableClient;
import network.platon.pid.contract.dto.DeployContractData;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import network.platon.pid.sdk.base.dto.AuthorityInfo;
import network.platon.pid.sdk.base.dto.Credential;
import network.platon.pid.sdk.client.ReloadClient;
import network.platon.pid.sdk.contract.service.ContractService;
import network.platon.pid.sdk.deploy.DeployContract;
import network.platon.pid.sdk.req.credential.CreateCredentialReq;
import network.platon.pid.sdk.req.pid.CreatePidReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.TransactionResp;
import network.platon.pid.sdk.resp.credential.CreateCredentialResp;
import network.platon.pid.sdk.resp.pid.CreatePidResp;
import network.platon.pid.sdk.service.*;
import network.platon.pid.sdk.service.impl.*;
import network.platon.pid.sdk.utils.PidUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@Slf4j
public class BaseTest {

    protected CredentialService credentialService = new CredentialServiceImpl();

    protected PresentationService presentationService = new PresentationServiceImpl();

    protected EvidenceService evidenceService = new EvidenceServiceImpl();

    protected VoteService agencyService = new VoteServiceImpl();

    protected PctService pctService = new PctServiceImpl();

    protected PidentityService pidService = new PidentityServiceImpl();

    protected BaseResp<?> resp;

    protected String adminPrivateKey;
    protected String adminAddress;
    protected String adminPid;
    protected String adminPublicKeyId;

    protected static int deployFlag = 0;

    @Before
    public void setup() {
        adminPrivateKey = PidConfig.getCONTRACT_PRIVATEKEY();
        log.debug("adminPrivateKey:{}", adminPrivateKey);

        Credentials credentials = Credentials.create(adminPrivateKey);
        adminAddress = credentials.getAddress();
        log.debug("adminAddress:{}", adminAddress);

        adminPid = PidUtils.convertAddressStrToPid(adminAddress);
        log.debug("adminPid:{}", adminPid);

        adminPublicKeyId = adminPid + "#keys-1";
        log.debug("adminPublicKeyId:{}", adminPublicKeyId);

        if (deployFlag == 0) {
            log.debug("Deploy contract start...");
            BaseResp<List<DeployContractData>> response = DeployContract.deployAllContract(adminPrivateKey, adminAddress);
            log.debug("Deploy contract result code:{}", response.getCode());
            if (response.checkSuccess()) {
                try {
                    for (DeployContractData deployContractData : response.getData()) {
                        switch (deployContractData.getContractNameValues()) {
                            case PID:
                                PropertyUtils.setProperty(PidConfig.getPidcontractname(), deployContractData.getContractAddress());
                                log.debug("PidContract address:{}", deployContractData.getContractAddress());
                                break;
                            case ROLE:
                                PropertyUtils.setProperty(PidConfig.getRolecontractname(), deployContractData.getContractAddress());
                                log.debug("RoleContract address:{}", deployContractData.getContractAddress());
                                break;
                            case AUTHORITY_CONTROLLER:
                                PropertyUtils.setProperty(PidConfig.getAuthoritycontrollercontractname(), deployContractData.getContractAddress());
                                log.debug("AuthorityController address:{}", deployContractData.getContractAddress());
                                break;
                            case AUTHORITY_DATA:
                                PropertyUtils.setProperty(PidConfig.getAuthoritydatacontractname(), deployContractData.getContractAddress());
                                log.debug("AuthorityData address:{}",deployContractData.getContractAddress());
                                break;
                            case PCT_CONTROLLER:
                                PropertyUtils.setProperty(PidConfig.getPctcontrollercontractname(), deployContractData.getContractAddress());
                                log.debug("PctController address:{}",deployContractData.getContractAddress());
                                break;
                            case PCT_DATA:
                                PropertyUtils.setProperty(PidConfig.getPctdatacontractname(), deployContractData.getContractAddress());
                                log.debug("PctData address:{}",deployContractData.getContractAddress());
                                break;
                            case CREDENTIAL:
                                PropertyUtils.setProperty(PidConfig.getCredentialcontractname(), deployContractData.getContractAddress());
                                log.debug("CredentialContract address:{}",deployContractData.getContractAddress());
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ReloadClient.deployContractData(response.getData());
                ContractService.init();
            }
            log.debug("Deploy contract end...");
            deployFlag++;
        }
    }

    @After
    public void after() {
        logResult(resp);
    }

    protected void logResult(BaseResp<?> resp) {
        System.out.println("UT result:" + JSONObject.toJSONString(resp));
        log.info("UT result:{}", JSONObject.toJSONString(resp));
    }

    protected void okResult(BaseResp<?> respInput) {
        resp = respInput;
        assertTrue(resp.checkSuccess());
    }

    protected void failedResult(BaseResp<?> respInput) {
        resp = respInput;
        assertTrue(resp.checkFail());
    }

    public boolean createIdentityByPrivateKey(BaseResp<?> resp, String privateKey) {
        ECKeyPair keyPair = AlgorithmHandler.createEcKeyPair(privateKey);
        RetryableClient retryableClient = new RetryableClient();
        retryableClient.init();
        Web3j web3j = retryableClient.getWeb3jWrapper().getWeb3j();
        Credentials credentials = Credentials.create(PidConfig.getCONTRACT_PRIVATEKEY());
        String publicKey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());
        String hexAddress = Keys.getAddress(publicKey);
        String address = Bech32.addressEncode(NetworkParameters.getHrp(), hexAddress);
        TransactionReceipt receipt = null;
        try {
            receipt = Transfer.sendFunds(
                            web3j, credentials, address,
                            BigDecimal.valueOf(80000000), Convert.Unit.PVON)
                    .send();
        }catch (Exception e) {
            log.error(
                    "Transfer failed, the address: {}, the exception: {}",
                    address, e
            );
            return false;
        }

        if(!receipt.isStatusOK()){
            log.error(
                    "Transfer failed, the address: {}",
                    address
            );

            return false;
        }

        CreatePidReq req = CreatePidReq.builder().privateKey(privateKey).build();
        BaseResp<CreatePidResp> createPidResp = pidService.createPid(req);
        resp = createPidResp;
        if (createPidResp.checkFail() && createPidResp.getCode() != RetEnum.RET_PID_IDENTITY_ALREADY_EXIST.getCode()) {
            return false;
        }
        return true;
    }

    protected Credential initCredential() {
        Map<String, Object> claim = new HashMap<>();
        claim.put("name", "zhangsan");
        claim.put("no", "12345");
        claim.put("data", "456");
        String context = "1";
        String issuer = adminPid;
        String pctId = "1000";
        String publicKeyId = "did:pid:lax1uqug0zq7rcxddndleq4ux2ft3tv6dqljphydrl#keys-1";
        String type = "VerifiableCredential";
        CreateCredentialReq req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(new Date(1691863929).getTime())
                .issuer(issuer).pctId(pctId).pid(adminPid).privateKey(adminPrivateKey).publicKeyId(publicKeyId)
                .type(type).build();
        resp = credentialService.createCredential(req);
        if (resp.checkFail()) {
            log.error("createCredential error {}", JSONObject.toJSONString(resp));
            return null;
        }
        log.debug("createCredential success");
        CreateCredentialResp createResp = (CreateCredentialResp) resp.getData();
        return createResp.getCredential();
    }

    protected BaseResp<CreatePidResp> createPidBase() {
        ECKeyPair keyPair = null;

        try {
            keyPair = Keys.createEcKeyPair();
        } catch (Exception e) {
            log.error("Failed to create EcKeyPair, exception: {}", e);
            return BaseResp.build(RetEnum.RET_SYS_ERROR);
        }

        RetryableClient retryableClient = new RetryableClient();
        retryableClient.init();
        Web3j web3j = retryableClient.getWeb3jWrapper().getWeb3j();
        Credentials credentials = Credentials.create(PidConfig.getCONTRACT_PRIVATEKEY());
        String publicKey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());
        String hexAddress = Keys.getAddress(publicKey);
        String address = Bech32.addressEncode(NetworkParameters.getHrp(), hexAddress);
        TransactionReceipt receipt = null;
        try {
            receipt = Transfer.sendFunds(
                            web3j, credentials, address,
                            BigDecimal.valueOf(80000000), Convert.Unit.PVON)
                    .send();
        }catch (Exception e) {
            log.error(
                    "Transfer failed, the address: {}, the exception: {}",
                    address, e
            );
            return TransactionResp.buildWith(RetEnum.RET_PID_CREATE_PID_ERROR.getCode(),
                    "Transfer failed, the address: " + address + ", the exception: " + e.toString());
        }

        if(!receipt.isStatusOK()){
            return TransactionResp.buildWith(RetEnum.RET_PID_CREATE_PID_ERROR.getCode(),
                    "Transfer failed, the address: " + address);
        }

        String privateKey = Numeric.toHexStringWithPrefix(keyPair.getPrivateKey());
        CreatePidReq req = CreatePidReq.builder().privateKey(privateKey).build();
        return pidService.createPid(req);
    }

    protected BaseResp<SetAuthorityResp> addAuthorityByPid(String pid) {

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

        return agencyService.addAuthorityIssuer(addAuthorityReq);
    }

}
