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
import network.platon.pid.common.utils.PropertyUtils;
import network.platon.pid.contract.client.RetryableClient;
import network.platon.pid.contract.dto.DeployContractData;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
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
import org.junit.After;
import org.junit.Before;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@Slf4j
public class BaseTest {


    static {
        NetworkParameters.init(210309L, "lat");
    }

    protected CredentialService credentialService = new CredentialServiceImpl();

    protected EvidenceService evidenceService = new EvidenceServiceImpl();

    protected VoteService voteService = new VoteServiceImpl();

    protected PctService pctService = new PctServiceImpl();

    protected PidentityService pidService = new PidentityServiceImpl();

    protected BaseResp<?> resp;

    protected String adminPrivateKey;
    protected String adminAddress;
    protected String adminServiceUrl;
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

        adminAddress = PidConfig.getADMIN_ADDRESS();
        log.debug("adminAddress:{}", adminAddress);

        adminServiceUrl = PidConfig.getADMIN_SERVICE_URL();
        log.debug("adminServiceUrl:{}", adminServiceUrl);

        if (deployFlag == 0) {
            log.debug("Deploy contract start...");
            BaseResp<List<DeployContractData>> response = DeployContract.deployAllContract(adminPrivateKey, adminAddress, adminServiceUrl);
            log.debug("Deploy contract result code:{}", response.getCode());
            if (response.checkSuccess()) {
                try {
                    for (DeployContractData deployContractData : response.getData()) {
                        switch (deployContractData.getContractNameValues()) {
                            case PID:
                                PropertyUtils.setProperty(PidConfig.getPidcontractname(), deployContractData.getContractAddress());
                                log.debug("PidContract address:{}", deployContractData.getContractAddress());
                                break;
                            case VOTE:
                                PropertyUtils.setProperty(PidConfig.getVotecontractname(), deployContractData.getContractAddress());
                                log.debug("VoteContract address:{}", deployContractData.getContractAddress());
                                break;
                            case PCT:
                                PropertyUtils.setProperty(PidConfig.getPctcontractname(), deployContractData.getContractAddress());
                                log.debug("Pct address:{}",deployContractData.getContractAddress());
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
        String publicKey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

        CreatePidReq req = CreatePidReq.builder().privateKey(privateKey).publicKey(publicKey).build();
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
        String pctId = "1000";
        String publicKeyId = "did:pid:lax1uqug0zq7rcxddndleq4ux2ft3tv6dqljphydrl#keys-1";
        String type = "VerifiableCredential";
        CreateCredentialReq req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(new Date(1691863929).getTime())
                .pctId(pctId).pid(adminPid).privateKey(adminPrivateKey).publicKeyId(publicKeyId)
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

}
