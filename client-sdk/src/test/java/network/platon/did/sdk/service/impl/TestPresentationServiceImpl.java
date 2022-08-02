package network.platon.did.sdk.service.impl;

import com.alibaba.fastjson.JSONObject;
import network.platon.did.common.utils.DateUtils;
import network.platon.did.sdk.BaseTest;
import network.platon.did.sdk.base.dto.*;
import network.platon.did.sdk.factory.PClient;
import network.platon.did.sdk.req.credential.CreateCredentialReq;
import network.platon.did.sdk.req.pct.CreatePctReq;
import network.platon.did.sdk.req.did.CreateDidReq;
import network.platon.did.sdk.req.presentation.CreatePresetationReq;
import network.platon.did.sdk.req.presentation.VerifyPresetationReq;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.credential.CreateCredentialResp;
import network.platon.did.sdk.resp.pct.CreatePctResp;
import network.platon.did.sdk.resp.presentation.CreatePresetationResp;
import org.junit.Test;

import java.util.*;

public class TestPresentationServiceImpl extends BaseTest{
	

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
		CreateDidReq createDidReq = CreateDidReq.builder().privateKey(adminPrivateKey).build();
		didService.createDid(createDidReq);

		String pctJson = "{\"properties\": { \"name\": { \"type\": \"string\" }, \"no\": { \"type\": \"string\" }, \"data\": { \"type\": \"string\" }}}";
		CreatePctReq createPctReq = CreatePctReq.builder()
				.pctjson(pctJson)
				.build();
		BaseResp<CreatePctResp> createPctRespBaseResp = pctService.registerPct(createPctReq);
		logResult(createPctRespBaseResp);

		Map<String, Object> claim = new HashMap<>();
		claim.put("name", "zhangsan");
		claim.put("no", "123");
		claim.put("data", "456");

		String context = "https://platon.network/";
		long expirationDate = DateUtils.convertUtcDateToTimeStamp("2080-08-04T13:35:49Z");
		long issuanceDate = new Date().getTime();
		String credentialType = "VerifiableCredential";

		CreateCredentialReq req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId(createPctRespBaseResp.getData().getPctId()).did(adminDid)
				.privateKey(adminPrivateKey).publicKeyId(adminPublicKeyId)
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
}
