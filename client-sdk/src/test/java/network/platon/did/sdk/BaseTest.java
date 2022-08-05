package network.platon.did.sdk;

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
import network.platon.did.common.config.DidConfig;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.common.utils.PropertyUtils;
import network.platon.did.contract.client.RetryableClient;
import network.platon.did.contract.dto.DeployContractData;
import network.platon.did.contract.dto.InitContractData;
import network.platon.did.csies.algorithm.AlgorithmHandler;
import network.platon.did.sdk.base.dto.Credential;
import network.platon.did.sdk.client.ReloadClient;
import network.platon.did.sdk.contract.service.ContractService;
import network.platon.did.sdk.deploy.DeployContract;
import network.platon.did.sdk.factory.PClient;
import network.platon.did.sdk.req.credential.CreateCredentialReq;
import network.platon.did.sdk.req.did.CreateDidReq;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.TransactionResp;
import network.platon.did.sdk.resp.credential.CreateCredentialResp;
import network.platon.did.sdk.resp.did.CreateDidResp;
import network.platon.did.sdk.service.*;
import network.platon.did.sdk.utils.DidUtils;
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

    protected CredentialService credentialService = PClient.createCredentialClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY()));

    protected EvidenceService evidenceService = PClient.createEvidenceClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY()));

    protected VoteService voteService = PClient.createAgencyClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY()));

    protected PctService pctService = PClient.createPctClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY()));

    protected DidentityService didService = PClient.createDidentityClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY()));

    protected BaseResp<?> resp;

    protected String adminPrivateKey;
    protected String adminAddress;
    protected String adminServiceUrl;
    protected String adminDid;
    protected String adminPublicKeyId;

    protected static int deployFlag = 0;

    @Before
    public void setup() {
        adminPrivateKey = DidConfig.getCONTRACT_PRIVATEKEY();
        log.debug("adminPrivateKey:{}", adminPrivateKey);

        Credentials credentials = Credentials.create(adminPrivateKey);
        adminAddress = credentials.getAddress();
        log.debug("adminAddress:{}", adminAddress);

        adminDid = DidUtils.convertAddressStrToDid(adminAddress);
        log.debug("adminDid:{}", adminDid);

        adminPublicKeyId = adminDid + "#keys-1";
        log.debug("adminPublicKeyId:{}", adminPublicKeyId);

        adminAddress = DidConfig.getADMIN_ADDRESS();
        log.debug("adminAddress:{}", adminAddress);

        adminServiceUrl = DidConfig.getADMIN_SERVICE_URL();
        log.debug("adminServiceUrl:{}", adminServiceUrl);

        if (deployFlag == 0) {
            log.debug("Deploy contract start...");
            BaseResp<List<DeployContractData>> response = DeployContract.deployAllContract(adminPrivateKey, adminAddress, adminServiceUrl);
            log.debug("Deploy contract result code:{}", response.getCode());
            if (response.checkSuccess()) {
                try {
                    for (DeployContractData deployContractData : response.getData()) {
                        switch (deployContractData.getContractNameValues()) {
                            case DID:
                                PropertyUtils.setProperty(DidConfig.getDidcontractname(), deployContractData.getContractAddress());
                                log.debug("DidContract address:{}", deployContractData.getContractAddress());
                                break;
                            case VOTE:
                                PropertyUtils.setProperty(DidConfig.getVotecontractname(), deployContractData.getContractAddress());
                                log.debug("VoteContract address:{}", deployContractData.getContractAddress());
                                break;
                            case PCT:
                                PropertyUtils.setProperty(DidConfig.getPctcontractname(), deployContractData.getContractAddress());
                                log.debug("Pct address:{}",deployContractData.getContractAddress());
                                break;
                            case CREDENTIAL:
                                PropertyUtils.setProperty(DidConfig.getCredentialcontractname(), deployContractData.getContractAddress());
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
        String hexAddress = Keys.getAddress(publicKey);
        String address = Bech32.addressEncode(NetworkParameters.getHrp(), hexAddress);

        RetryableClient retryableClient = new RetryableClient();
        retryableClient.init();
        Web3j web3j = retryableClient.getWeb3jWrapper().getWeb3j();

        Credentials credentials = Credentials.create(DidConfig.getCONTRACT_PRIVATEKEY());
        TransactionReceipt receipt = null;
        try {
            receipt = Transfer.sendFunds(
                            web3j, credentials, address,
                            BigDecimal.valueOf(100), Convert.Unit.PVON)
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

        CreateDidReq req = CreateDidReq.builder().privateKey(privateKey).publicKey(publicKey).build();
        BaseResp<CreateDidResp> createDidResp = didService.createDid(req);
        resp = createDidResp;
        if (createDidResp.checkFail() && createDidResp.getCode() != RetEnum.RET_DID_IDENTITY_ALREADY_EXIST.getCode()) {
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
                .pctId(pctId).did(adminDid).privateKey(adminPrivateKey).publicKeyId(publicKeyId)
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

    protected BaseResp<CreateDidResp> createDidBase() {
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
        Credentials credentials = Credentials.create(DidConfig.getCONTRACT_PRIVATEKEY());

        String publicKey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());
        String hexAddress = Keys.getAddress(publicKey);
        String address = Bech32.addressEncode(NetworkParameters.getHrp(), hexAddress);
        TransactionReceipt receipt = null;
        try {
            receipt = Transfer.sendFunds(
                            web3j, credentials, address,
                            BigDecimal.valueOf(100), Convert.Unit.PVON)
                    .send();
        }catch (Exception e) {
            log.error(
                    "Transfer failed, the address: {}, the exception: {}",
                    address, e
            );
            return TransactionResp.buildWith(RetEnum.RET_DID_CREATE_DID_ERROR.getCode(),
                    "Transfer failed, the address: " + address + ", the exception: " + e.toString());
        }

        if(!receipt.isStatusOK()){
            return TransactionResp.buildWith(RetEnum.RET_DID_CREATE_DID_ERROR.getCode(),
                    "Transfer failed, the address: " + address);
        }

        String privateKey = Numeric.toHexStringWithPrefix(keyPair.getPrivateKey());
        CreateDidReq req = CreateDidReq.builder().privateKey(privateKey).publicKey(publicKey).build();
        return didService.createDid(req);
    }

}
