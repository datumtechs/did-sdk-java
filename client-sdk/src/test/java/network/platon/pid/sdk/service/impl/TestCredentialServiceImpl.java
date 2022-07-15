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
import network.platon.pid.common.constant.VpOrVcPoofKey;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.common.utils.DateUtils;
import network.platon.pid.contract.client.RetryableClient;
import network.platon.pid.contract.dto.InitContractData;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import network.platon.pid.csies.utils.ConverDataUtils;
import network.platon.pid.sdk.BaseTest;
import network.platon.pid.sdk.base.dto.AuthorityInfo;
import network.platon.pid.sdk.base.dto.Credential;
import network.platon.pid.sdk.factory.PClient;
import network.platon.pid.sdk.req.credential.CreateCredentialReq;
import network.platon.pid.sdk.req.credential.CreateSelectCredentialReq;
import network.platon.pid.sdk.req.credential.VerifyCredentialReq;
import network.platon.pid.sdk.req.pct.CreatePctReq;
import network.platon.pid.sdk.req.pid.CreatePidReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.credential.CreateCredentialResp;
import network.platon.pid.sdk.resp.pct.CreatePctResp;
import network.platon.pid.sdk.resp.pid.CreatePidResp;
import network.platon.pid.sdk.utils.PidUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
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
		String pid = this.testCreatePid(privateKey);

		ECKeyPair keyPair = Keys.createEcKeyPair();
		String issuerPriKey = keyPair.getPrivateKey().toString(16);
		String issuer = testCreatePid(issuerPriKey);
		String issuerPubKeyId = issuer + "#keys-1";

		// Add an authority issuer
		testCreateAuth(issuer);

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
				.issuer(issuer).pctId(pctId).pid(pid).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId)
				.type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);

		assertTrue(resp.checkSuccess());
	}

	@Test
	public void test_createCredentialParameters() throws Exception {
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String pid = this.testCreatePid(privateKey);

		String issuerPriKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String issuer = testCreatePid(issuerPriKey);
		String issuerPubKeyId = issuer + "#keys-1";

		// Add an authority issuer
		testCreateAuth(issuer);

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
				.issuer(issuer).pctId(pctId).pid(pid).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 2.valid context is empty
		// req =
		// CreateCredentialReq.builder().claim(claim).context("").expirationDate(expirationDate).issuanceDate(issuanceDate).issuer(issuer)
		// .pctId(pctId).pid(pid).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		// resp = PClient.createCredentialClient().createCredential(req);
		// assertTrue(resp.checkFail() &&
		// resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 3.valid expirationDate is 0
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(0L).issuer(issuer).pctId(pctId)
				.pid(pid).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

//		// 4.valid issuanceDate is 0
//		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuer(issuer).pctId(pctId)
//				.pid(pid).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
//		resp = PClient.createCredentialClient().createCredential(req);
//		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 5.valid issuer format error
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
				.issuer("11111111111111111111111111111111111111111111111111").pctId(pctId).pid(pid).privateKey(issuerPriKey)
				.publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 6.valid issuer is not exists
		String issuer2 = PidUtils.generatePid(Numeric.toHexStringWithPrefix(Keys.createEcKeyPair().getPublicKey()));
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuer(issuer2)
				.pctId(pctId).pid(pid).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_PID_IDENTITY_NOTEXIST.getCode()));

		// 7.valid pctId is too long
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuer(issuer)
				.pctId(String.valueOf(Long.MAX_VALUE)).pid(pid).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_PCT_QUERY_BY_ID_ERROR.getCode()));

		// 8.valid pctId is not exists
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuer(issuer)
				.pctId("11223344").pid(pid).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_PCT_QUERY_JSON_NOT_FOUND_ERROR.getCode()));

		// 9.valid pid format error
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuer(issuer)
				.pctId(pctId).pid("11111111111111111111111111111111111111111111111111").privateKey(issuerPriKey).publicKeyId(issuerPubKeyId)
				.type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 10.valid pid is not exists
		String pid2 = PidUtils.generatePid(Numeric.toHexStringWithPrefix(Keys.createEcKeyPair().getPublicKey()));
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuer(issuer)
				.pctId(pctId).pid(pid2).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_CREDENTIAL_PID_NOT_FOUND.getCode()));

		// 11.valid privateKey is empty
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuer(issuer)
				.pctId(pctId).pid(pid).privateKey("").publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 12.valid privateKey format error
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuer(issuer)
				.pctId(pctId).pid(pid).privateKey(issuerPriKey + "ssss").publicKeyId(issuerPubKeyId).type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 13.valid issuerPubKeyId is not exists
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuer(issuer)
				.pctId(pctId).pid(pid).privateKey(issuerPriKey).publicKeyId(pid2 + "#keys-1").type(credentialType).build();
		resp = PClient.createCredentialClient().createCredential(req);
		assertTrue(resp.checkFail() && resp.getCode().equals(RetEnum.RET_CREDENTIAL_PUBLICKEY_NOT_AUTH.getCode()));

		// 14. valid type is empty
		// req =
		// CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuanceDate(issuanceDate).issuer(issuer)
		// .pctId(pctId).pid(pid).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type("").build();
		// resp = PClient.createCredentialClient.createCredential(req);
		// assertTrue(resp.checkFail() &&
		// resp.getCode().equals(RetEnum.RET_COMMON_PARAM_INVALLID.getCode()));

		// 15.success
		req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate).issuer(issuer)
				.pctId(pctId).pid(pid).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).type(credentialType).build();
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
		String issuer = PidUtils.generatePid(Numeric.toHexStringWithPrefix(Keys.createEcKeyPair().getPublicKey()));
		tmp.setIssuer(issuer);
		credentialReq = VerifyCredentialReq.builder().credential(tmp).build();
		resp = PClient.createCredentialClient().verifyCredential(credentialReq);
		assertTrue("the issuer is not extsts", resp.checkFail() && resp.getCode().equals(RetEnum.RET_PID_IDENTITY_NOTEXIST.getCode()));

		// the holder is other's
		Credential tmp2 = ConverDataUtils.clone(credential);
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String holder = testCreatePid(privateKey);
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
			String pid = this.testCreatePid(privateKey);

			String issuerPriKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
			String issuer = testCreatePid(issuerPriKey);
			String issuerPubKeyId = issuer + "#keys-1";

			// Add an authority issuer
			testCreateAuth(issuer);

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
					.issuer(issuer).pctId(pctId).pid(pid).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId)
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

	private String testCreatePid(String privateKey) throws Exception {
		ECKeyPair keyPair = AlgorithmHandler.createEcKeyPair(privateKey);
		RetryableClient retryableClient = new RetryableClient();
		retryableClient.init();
		Web3j web3j = retryableClient.getWeb3jWrapper().getWeb3j();
		Credentials credentials = Credentials.create(PidConfig.getCONTRACT_PRIVATEKEY());
		String publicKey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());
		String hexAddress = Keys.getAddress(publicKey);
		String address = Bech32.addressEncode(NetworkParameters.getHrp(), hexAddress);
		TransactionReceipt receipt = null;
		receipt = Transfer.sendFunds(
							web3j, credentials, address,
							BigDecimal.valueOf(80000000), Convert.Unit.PVON)
					.send();
		if(!receipt.isStatusOK()){
			String msg = "Create pid error";
			logger.error("Create pid error");

			throw new Exception(msg);
		}

		CreatePidReq req = CreatePidReq.builder().privateKey(privateKey).build();
		BaseResp<CreatePidResp> createPidResp = PClient.createPidentityClient().createPid(req);
		if (createPidResp.checkFail()) {
			String msg = JSONObject.toJSONString(createPidResp);
			logger.error("Create pid error,error msg:{}", msg);

			throw new Exception(msg);
		}
		return createPidResp.getData().getPid();
	}

	private void testCreateAuth(String pid) throws Exception {
		AuthorityInfo authorityInfo = new AuthorityInfo();
		authorityInfo.setPid(pid);
		authorityInfo.setName("Authority Issuer" + System.currentTimeMillis());

		authorityInfo.setCreateTime(DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp()));
		authorityInfo.setAccumulate(BigInteger.valueOf(0));
		authorityInfo.setExtra(new HashMap<String, Object>());

		SetAuthorityReq req = SetAuthorityReq.builder().privateKey(adminPrivateKey).authority(authorityInfo).build();

		BaseResp<SetAuthorityResp> createAuthResp = PClient.createAgencyClient().addAuthorityIssuer(req);
		if (createAuthResp.checkFail()) {
			String msg = JSONObject.toJSONString(createAuthResp);
			logger.error("Add authority issuer error,error msg:{}", msg);

			throw new Exception(msg);
		}
	}

	private String testCreatePct(String pid, String privateKey, String pctJson, Integer type) throws Exception {
		CreatePctReq req = CreatePctReq.builder().pid(pid).pctjson(pctJson).build();
		BaseResp<CreatePctResp> createPctBaseResp = PClient.createPctClient(new InitContractData(privateKey)).registerPct(req);
		if (createPctBaseResp.checkFail()) {
			String msg = JSONObject.toJSONString(createPctBaseResp);
			logger.error("Register pct error,error msg:{}", msg);

			throw new Exception(msg);
		}
		return createPctBaseResp.getData().getPctId();
	}
}
