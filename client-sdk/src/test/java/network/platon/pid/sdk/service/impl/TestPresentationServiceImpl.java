package network.platon.pid.sdk.service.impl;

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
import network.platon.pid.common.config.PidConfig;
import network.platon.pid.common.utils.DateUtils;
import network.platon.pid.contract.client.RetryableClient;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import network.platon.pid.sdk.BaseTest;
import network.platon.pid.sdk.base.dto.*;
import network.platon.pid.sdk.factory.PClient;
import network.platon.pid.sdk.req.credential.CreateCredentialReq;
import network.platon.pid.sdk.req.pct.CreatePctReq;
import network.platon.pid.sdk.req.pid.CreatePidReq;
import network.platon.pid.sdk.req.presentation.CreatePresetationReq;
import network.platon.pid.sdk.req.presentation.VerifyPresetationReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.credential.CreateCredentialResp;
import network.platon.pid.sdk.resp.pct.CreatePctResp;
import network.platon.pid.sdk.resp.pid.CreatePidResp;
import network.platon.pid.sdk.resp.presentation.CreatePresetationResp;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class TestPresentationServiceImpl extends BaseTest{

	private static Logger logger = LoggerFactory.getLogger(TestEvidenceServiceImpl.class);

	@Test
	public void test_createPresentation() throws Exception {
		Credential credential = this.createData();
		BaseResp<CreatePresetationResp> createPresetationRespBaseResp = this.createPresentation(credential);
		okResult(createPresetationRespBaseResp);
	}

	@Test
	public void verifyPresentation()throws Exception {
		Credential credential = this.createData();
		BaseResp<CreatePresetationResp> createPresetationRespBaseResp = this.createPresentation(credential);
		PresentationPolicy policy = new PresentationPolicy();
		Map<String, ClaimPolicy> policyMap = new HashMap<String, ClaimPolicy>();
		ClaimPolicy claimPolicy = new ClaimPolicy();
		Map<String, Object> selectMap = new HashMap<>();
		selectMap.put("name", 1);
		selectMap.put("no", 0);
		selectMap.put("data", 0);
		claimPolicy.setDisclosedFieldsJson(JSONObject.toJSONString(selectMap));
		policyMap.put("1000", claimPolicy);
		policy.setPolicys(policyMap);
		Challenge challenge = Challenge.fromJson("{\"nonce\":\""+ createPresetationRespBaseResp.getData().getPresentation().obtainNonce() +"\",\"pid\":\""+ adminPid +"\"}");
		Presentation presentation = createPresetationRespBaseResp.getData().getPresentation();
		VerifyPresetationReq req = VerifyPresetationReq.builder()
				.pid(adminPid)
				.challenge(challenge)
				.policy(policy)
				.presentation(presentation).build();
		okResult(PClient.createPresentationClient().verifyPresentation(req));
	}


	private Credential createData() throws Exception{
		// Create pid by privateKey
		String holderPrivateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String holderPid = testCreatePid(holderPrivateKey);

		// create did
		String issuerPrivateKey = adminPrivateKey;
		ECKeyPair keyPair = AlgorithmHandler.createEcKeyPair(issuerPrivateKey);
		String issuerPublicKey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		CreatePidReq createPidReq = CreatePidReq.builder().privateKey(issuerPrivateKey).publicKey(issuerPublicKey).build();
		BaseResp<CreatePidResp> createPidResp = pidService.createPid(createPidReq);

		// register pct
		String pctJson = "{\"properties\": { \"name\": { \"type\": \"string\" }, \"no\": { \"type\": \"string\" }, \"data\": { \"type\": \"string\" }}}";
		String str = "This is a String";
		CreatePctReq createPctReq = CreatePctReq.builder()
				.privateKey(issuerPrivateKey)
				.pctjson(pctJson)
				.extra(str.getBytes())
				.build();
		BaseResp<CreatePctResp> createPctRespBaseResp = pctService.registerPct(createPctReq);
		logResult(createPctRespBaseResp);

		// create credential
		Map<String, Object> claim = new HashMap<>();
		claim.put("name", "zhangsan");
		claim.put("no", "123");
		claim.put("data", "456");

		String context = "https://platon.network/";
		long expirationDate = DateUtils.convertUtcDateToTimeStamp("2080-08-04T13:35:49Z");
		String credentialType = "VerifiableCredential";

		CreateCredentialReq req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId(createPctRespBaseResp.getData().getPctId()).pid(holderPid)
				.privateKey(issuerPrivateKey).publicKeyId(createPidResp.getData().getPid() + "#keys-1").issuer(createPidResp.getData().getPid())
				.type(credentialType).build();
		BaseResp<CreateCredentialResp> createCredentialRespBaseResp = PClient.createCredentialClient().createCredential(req);
		return  createCredentialRespBaseResp.getData().getCredential();
	}

	private BaseResp<CreatePresetationResp>  createPresentation(Credential credential){
		List<Credential> credentials = new ArrayList<Credential>();
		credentials.add(credential);
		Challenge challenge = Challenge.create(adminPid, "123456");
		System.out.println(challenge.toRawData());
		PidAuthentication pidAuthentication = new PidAuthentication();
		pidAuthentication.setPid(adminPid);
		pidAuthentication.setPrivateKey(adminPrivateKey);
		pidAuthentication.setPublicKeyId(adminPublicKeyId);
		PresentationPolicy policy = new PresentationPolicy();
		Map<String, ClaimPolicy> policyMap = new HashMap<String, ClaimPolicy>();
		ClaimPolicy claimPolicy = new ClaimPolicy();
		Map<String, Object> selectMap = new HashMap<>();
		selectMap.put("name", 1);
		selectMap.put("no", 0);
		selectMap.put("data", 0);
		claimPolicy.setDisclosedFieldsJson(JSONObject.toJSONString(selectMap));
		policyMap.put("1000", claimPolicy);
		policy.setPolicys(policyMap);
		CreatePresetationReq req = CreatePresetationReq.builder()
				.authentication(pidAuthentication)
				.challenge(challenge)
				.policy(policy)
				.credentials(credentials).build();
		return PClient.createPresentationClient().createPresentation(req);
	}

	private String testCreatePid(String privateKey) throws Exception {

		RetryableClient retryableClient = new RetryableClient();
		retryableClient.init();
		Web3j web3j = retryableClient.getWeb3jWrapper().getWeb3j();
		Credentials credentials = Credentials.create(PidConfig.getCONTRACT_PRIVATEKEY());

		ECKeyPair keyPair = AlgorithmHandler.createEcKeyPair(privateKey);
		String publicKey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());
		String hexAddress = Keys.getAddress(publicKey);
		String address = Bech32.addressEncode(NetworkParameters.getHrp(), hexAddress);

		TransactionReceipt receipt = null;
		receipt = Transfer.sendFunds(
						web3j, credentials, address,
						BigDecimal.valueOf(1), Convert.Unit.KPVON)
				.send();
		if(!receipt.isStatusOK()){
			String msg = "Create pid error";
			logger.error("Create pid error");

			throw new Exception(msg);
		}

		CreatePidReq req = CreatePidReq.builder().privateKey(privateKey).publicKey(publicKey).build();
		BaseResp<CreatePidResp> createPidResp = PClient.createPidentityClient().createPid(req);
		if (createPidResp.checkFail()) {
			String msg = JSONObject.toJSONString(createPidResp);
			logger.error("Create pid error,error msg:{}", msg);

			throw new Exception(msg);
		}
		return createPidResp.getData().getPid();
	}
}
