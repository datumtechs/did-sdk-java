package network.platon.did.sdk.service.impl;

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
import network.platon.did.common.config.DidConfig;
import network.platon.did.common.utils.DateUtils;
import network.platon.did.contract.client.RetryableClient;
import network.platon.did.csies.algorithm.AlgorithmHandler;
import network.platon.did.sdk.BaseTest;
import network.platon.did.sdk.base.dto.*;
import network.platon.did.sdk.factory.PClient;
import network.platon.did.sdk.req.credential.CreateCredentialReq;
import network.platon.did.sdk.req.did.CreateDidReq;
import network.platon.did.sdk.req.pct.CreatePctReq;
import network.platon.did.sdk.req.presentation.CreatePresetationReq;
import network.platon.did.sdk.req.presentation.VerifyPresetationReq;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.credential.CreateCredentialResp;
import network.platon.did.sdk.resp.did.CreateDidResp;
import network.platon.did.sdk.resp.pct.CreatePctResp;
import network.platon.did.sdk.resp.presentation.CreatePresetationResp;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		Challenge challenge = Challenge.fromJson("{\"nonce\":\""+ createPresetationRespBaseResp.getData().getPresentation().obtainNonce() +"\",\"did\":\""+ adminDid +"\"}");
		Presentation presentation = createPresetationRespBaseResp.getData().getPresentation();
		VerifyPresetationReq req = VerifyPresetationReq.builder()
				.did(adminDid)
				.challenge(challenge)
				.policy(policy)
				.presentation(presentation).build();
		okResult(PClient.createPresentationClient().verifyPresentation(req));
	}


	private Credential createData() throws Exception{
		// Create did by privateKey
		String holderPrivateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String holderDid = testCreateDid(holderPrivateKey);

		// create did
		String issuerPrivateKey = adminPrivateKey;
		ECKeyPair keyPair = AlgorithmHandler.createEcKeyPair(issuerPrivateKey);
		String issuerPublicKey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		CreateDidReq createDidReq = CreateDidReq.builder().privateKey(issuerPrivateKey).publicKey(issuerPublicKey).build();
		BaseResp<CreateDidResp> createDidResp = didService.createDid(createDidReq);

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
				.pctId(createPctRespBaseResp.getData().getPctId()).did(holderDid)
				.privateKey(issuerPrivateKey).publicKeyId(createDidResp.getData().getDid() + "#keys-1").issuer(createDidResp.getData().getDid())
				.type(credentialType).build();
		BaseResp<CreateCredentialResp> createCredentialRespBaseResp = PClient.createCredentialClient().createCredential(req);
		return  createCredentialRespBaseResp.getData().getCredential();
	}

	private BaseResp<CreatePresetationResp>  createPresentation(Credential credential){
		List<Credential> credentials = new ArrayList<Credential>();
		credentials.add(credential);
		Challenge challenge = Challenge.create(adminDid, "123456");
		System.out.println(challenge.toRawData());
		DidAuthentication didAuthentication = new DidAuthentication();
		didAuthentication.setDid(adminDid);
		didAuthentication.setPrivateKey(adminPrivateKey);
		didAuthentication.setPublicKeyId(adminPublicKeyId);
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
				.authentication(didAuthentication)
				.challenge(challenge)
				.policy(policy)
				.credentials(credentials).build();
		return PClient.createPresentationClient().createPresentation(req);
	}

	private String testCreateDid(String privateKey) throws Exception {

		RetryableClient retryableClient = new RetryableClient();
		retryableClient.init();
		Web3j web3j = retryableClient.getWeb3jWrapper().getWeb3j();
		Credentials credentials = Credentials.create(DidConfig.getCONTRACT_PRIVATEKEY());

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
			String msg = "Create did error";
			logger.error("Create did error");

			throw new Exception(msg);
		}

		CreateDidReq req = CreateDidReq.builder().privateKey(privateKey).publicKey(publicKey).build();
		BaseResp<CreateDidResp> createDidResp = PClient.createDidentityClient().createDid(req);
		if (createDidResp.checkFail()) {
			String msg = JSONObject.toJSONString(createDidResp);
			logger.error("Create did error,error msg:{}", msg);

			throw new Exception(msg);
		}
		return createDidResp.getData().getDid();
	}
}
