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
import network.platon.did.common.constant.VpOrVcPoofKey;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.common.utils.DateUtils;
import network.platon.did.contract.client.RetryableClient;
import network.platon.did.contract.dto.InitContractData;
import network.platon.did.csies.algorithm.AlgorithmHandler;
import network.platon.did.csies.utils.ConverDataUtils;
import network.platon.did.sdk.BaseTest;
import network.platon.did.sdk.base.dto.Credential;
import network.platon.did.sdk.factory.PClient;
import network.platon.did.sdk.req.credential.CreateCredentialReq;
import network.platon.did.sdk.req.credential.CreateSelectCredentialReq;
import network.platon.did.sdk.req.credential.VerifyCredentialReq;
import network.platon.did.sdk.req.pct.CreatePctReq;
import network.platon.did.sdk.req.did.CreateDidReq;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.credential.CreateCredentialResp;
import network.platon.did.sdk.resp.pct.CreatePctResp;
import network.platon.did.sdk.resp.did.CreateDidResp;
import network.platon.did.sdk.utils.DidUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TestCredentialServiceImpl extends BaseTest {

	private static Logger logger = LoggerFactory.getLogger(TestCredentialServiceImpl.class);

	private Integer type;
	private String pctJson;

	@Before
	@Override
	public void setup() {
		super.setup();
		type = 1;
		pctJson = "{\"properties\": { \"name\": { \"type\": \"string\" }, \"no\": { \"type\": \"string\" }, \"data\": { \"type\": \"string\" }}}";
	}

	@Test
	public void test_createCredential() throws Exception {
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String did = this.testCreateDid(privateKey);

		ECKeyPair keyPair = Keys.createEcKeyPair();
		String issuerPriKey = keyPair.getPrivateKey().toString(16);
		String issuer = testCreateDid(issuerPriKey);
		String issuerPubKeyId = issuer + "#keys-1";

		// Create pct data in PlatON
		String pctId = testCreatePct(issuer, issuerPriKey, pctJson, type);

		Map<String, Object> claim = new HashMap<>();
		claim.put("name", "zhangsan");
		claim.put("no", "123");
		claim.put("data", "456");

		String context = "https://platon.network/";
		long expirationDate = new Date(1691863929).getTime();
		long issuanceDate = new Date().getTime();
		String credentialType = "VerifiableCredential";

		CreateCredentialReq req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId(pctId).did(did).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId)
				.type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);

		assertTrue(resp.checkSuccess());
	}

	@Test
	public void test_createCredentialParameters() throws Exception {
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String did = this.testCreateDid(privateKey);

		String issuerPriKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String issuer = testCreateDid(issuerPriKey);
		String issuerPubKeyId = issuer + "#keys-1";

		// Create pct data in PlatON
		String pctId = testCreatePct(issuer, issuerPriKey, pctJson, type);

		Map<String, Object> claim = new HashMap<>();
		claim.put("name", "zhangsan");
		claim.put("no", "123");
		claim.put("data", "456");

		String context = "https://platon.network/";
		long expirationDate = new Date(1691863929).getTime();
		long issuanceDate = new Date().getTime();
		String credentialType = "VerifiableCredential";

		// 1.valid claim is null
		CreateCredentialReq req = CreateCredentialReq.builder().claim(null).context(context).expirationDate(expirationDate)
				.pctId(pctId).did(did).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 2.valid context is empty
		// req =
		// CreateCredentialReq.builder().claim(claim).context("").expirationDate(expirationDate).issuanceDate(issuanceDate).issuer(issuer)
		// .pctId(pctId).did(did).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		// resp = PClient.createCredentialClient().createCredential(req);
		// assertTrue(resp.checkFail() &&
		// resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 3.valid expirationDate is 0
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(0L).pctId(pctId)
				.did(did).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

//		// 4.valid issuanceDate is 0
//		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuer(issuer).pctId(pctId)
//				.did(did).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
//		resp = PClient.createCredentialClient().createCredential(req);
//		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 5.valid issuer format error
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId(pctId).did(did).privateKey(issuerPriKey)
				.publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 6.valid issuer is not exists
		String issuer2 = DidUtils.generateDid(Numeric.toHexStringWithPrefix(Keys.createEcKeyPair().getPublicKey()));
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId(pctId).did(did).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_DID_IDENTITY_NOTEXIST.getCode()));

		// 7.valid pctId is too long
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId(String.valueOf(Long.MAX_VALUE)).did(did).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_PCT_QUERY_BY_ID_ERROR.getCode()));

		// 8.valid pctId is not exists
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId("11223344").did(did).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_PCT_QUERY_JSON_NOT_FOUND_ERROR.getCode()));

		// 9.valid did format error
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId(pctId).did("11111111111111111111111111111111111111111111111111").privateKey(issuerPriKey).publicKeyId(issuerPubKeyId)
				.type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 10.valid did is not exists
		String did2 = DidUtils.generateDid(Numeric.toHexStringWithPrefix(Keys.createEcKeyPair().getPublicKey()));
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId(pctId).did(did2).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_CREDENTIAL_DID_NOT_FOUND.getCode()));

		// 11.valid privateKey is empty
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId(pctId).did(did).privateKey("").publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 12.valid privateKey format error
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId(pctId).did(did).privateKey(issuerPriKey + "ssss").publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 13.valid issuerPubKeyId is not exists
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId(pctId).did(did).privateKey(issuerPriKey).publicKeyId(did2 + "#keys-1").type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_CREDENTIAL_PUBLICKEY_NOT_AUTH.getCode()));

		// 14. valid type is empty
		// req =
		// CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuanceDate(issuanceDate).issuer(issuer)
		// .pctId(pctId).did(did).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type("").build();
		// resp = PClient.createCredentialClient.createCredential(req);
		// assertTrue(resp.checkFail() &&
		// resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 15.success
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.pctId(pctId).did(did).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkSuccess());
	}

	@Test
	public void test_verifyCredential() throws Exception {

		Credential credential = this.testCreateCredential();

		VerifyCredentialReq credentialReq = VerifyCredentialReq.builder().credential(credential).build();

		resp = PClient.createCredentialClient().verifyCredential(credentialReq);

		assertTrue(resp.checkSuccess());
	}

	@Test
	public void test_verifyCredentialParameters() throws Exception {

		Credential credential = this.testCreateCredential();

		// credential is null
		VerifyCredentialReq credentialReq = VerifyCredentialReq.builder().credential(null).build();
		resp = PClient.createCredentialClient().verifyCredential(credentialReq);
		assertTrue("credential is null", resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		Credential tmp = ConverDataUtils.clone(credential);
		// the issuer is not extsts
		String issuer = DidUtils.generateDid(Numeric.toHexStringWithPrefix(Keys.createEcKeyPair().getPublicKey()));
		tmp.setIssuer(issuer);
		credentialReq = VerifyCredentialReq.builder().credential(tmp).build();
		resp = PClient.createCredentialClient().verifyCredential(credentialReq);
		assertTrue("the issuer is not extsts", resp.checkFail() && resp.getCode().equals(RetEnum.RET_DID_IDENTITY_NOTEXIST.getCode()));

		// the holder is other's
		Credential tmp2 = ConverDataUtils.clone(credential);
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String holder = testCreateDid(privateKey);
		tmp2.setHolder(holder);
		credentialReq = VerifyCredentialReq.builder().credential(tmp2).build();
		resp = PClient.createCredentialClient().verifyCredential(credentialReq);
		assertTrue("the holder is other's", resp.checkFail() && resp.getCode().equals(RetEnum.RET_CREDENTIAL_VERIFY_ERROR.getCode()));

		// update claimData
		Credential tmp3 = ConverDataUtils.clone(credential);
		tmp3.getClaimData().put("abc", "123");
		credentialReq = VerifyCredentialReq.builder().credential(tmp3).build();
		resp = PClient.createCredentialClient().verifyCredential(credentialReq);
		assertTrue("update claimData", resp.checkFail() && resp.getCode().equals(RetEnum.RET_CREDENTIAL_VERIFY_ERROR.getCode()));

		//update proof
		Credential tmp4 = ConverDataUtils.clone(credential);
		tmp4.getProof().remove(VpOrVcPoofKey.PROOF_CTEATED);
		credentialReq = VerifyCredentialReq.builder().credential(tmp4).build();
		resp = PClient.createCredentialClient().verifyCredential(credentialReq);
		assertTrue("update proof", resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_PROOF_INVALID.getCode()));
	}

	@Test
	public void test_createSelectCredential() {
		Credential credential = this.testCreateCredential();

		Map<String, Object> selectMap = new HashMap<>();
		selectMap.put("name", 1);
		selectMap.put("data", 0);
		selectMap.put("no", 0);
		CreateSelectCredentialReq req = CreateSelectCredentialReq.builder().credential(credential).selectMap(selectMap).build();
		resp = PClient.createCredentialClient().createSelectCredential(req);
		assertTrue(resp.checkSuccess());

		req = CreateSelectCredentialReq.builder().credential(null).selectMap(selectMap).build();
		resp = PClient.createCredentialClient().createSelectCredential(req);
		assertTrue(resp.checkFail());

		req = CreateSelectCredentialReq.builder().credential(credential).selectMap(null).build();
		resp = PClient.createCredentialClient().createSelectCredential(req);
		assertTrue(resp.checkFail());
		
		credential.getProof().clear();
		credential.getProof().remove(VpOrVcPoofKey.PROOF_CTEATED);
		req = CreateSelectCredentialReq.builder().credential(credential).selectMap(selectMap).build();
		resp = PClient.createCredentialClient().createSelectCredential(req);
		assertTrue(resp.checkFail());
	}

	private Credential testCreateCredential() {
		try {
			String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
			String did = this.testCreateDid(privateKey);

			String issuerPriKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
			String issuer = testCreateDid(issuerPriKey);
			String issuerPubKeyId = issuer + "#keys-1";

			// Create pct data in PlatON
			String pctId = testCreatePct(issuer, issuerPriKey, pctJson, type);

			Map<String, Object> claim = new HashMap<>();
			claim.put("name", "zhangsan");
			claim.put("no", "123");
			claim.put("data", "456");

			String context = "https://platon.network/";
			long expirationDate = DateUtils.convertUtcDateToTimeStamp("2080-08-04T13:35:49Z");
			long issuanceDate = new Date().getTime();
			String credentialType = "VerifiableCredential";

			CreateCredentialReq req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
					.pctId(pctId).did(did).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId)
					.type(credentialType).build();
			BaseResp<CreateCredentialResp> resp = PClient.createCredentialClient().createCredential(req);
			if (resp.checkSuccess()) return resp.getData().getCredential();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String testCreateDid(String privateKey) throws Exception {
		ECKeyPair keyPair = AlgorithmHandler.createEcKeyPair(privateKey);
		RetryableClient retryableClient = new RetryableClient();
		retryableClient.init();
		Web3j web3j = retryableClient.getWeb3jWrapper().getWeb3j();
		Credentials credentials = Credentials.create(DidConfig.getCONTRACT_PRIVATEKEY());

		String publicKey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());
		String hexAddress = Keys.getAddress(publicKey);
		String address = Bech32.addressEncode(NetworkParameters.getHrp(), hexAddress);
		TransactionReceipt receipt = null;
		receipt = Transfer.sendFunds(
							web3j, credentials, address,
							BigDecimal.valueOf(80000000), Convert.Unit.PVON)
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


	private String testCreatePct(String did, String privateKey, String pctJson, Integer type) throws Exception {
		String str = "test create pct";
		CreatePctReq req = CreatePctReq.builder().privateKey(DidConfig.getCONTRACT_PRIVATEKEY()).pctjson(pctJson).extra(str.getBytes()).build();
		BaseResp<CreatePctResp> createPctBaseResp = PClient.createPctClient(new InitContractData(privateKey)).registerPct(req);
		if (createPctBaseResp.checkFail()) {
			String msg = JSONObject.toJSONString(createPctBaseResp);
			logger.error("Register pct error,error msg:{}", msg);

			throw new Exception(msg);
		}
		return createPctBaseResp.getData().getPctId();
	}
}
