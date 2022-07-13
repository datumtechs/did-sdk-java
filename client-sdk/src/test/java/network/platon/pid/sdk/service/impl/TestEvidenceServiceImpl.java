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
import network.platon.pid.contract.dto.InitContractData;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import network.platon.pid.sdk.BaseTest;
import network.platon.pid.sdk.base.dto.AuthorityInfo;
import network.platon.pid.sdk.base.dto.Credential;
import network.platon.pid.sdk.factory.PClient;
import network.platon.pid.sdk.req.agency.SetAuthorityReq;
import network.platon.pid.sdk.req.credential.CreateCredentialReq;
import network.platon.pid.sdk.req.evidence.*;
import network.platon.pid.sdk.req.pct.CreatePctReq;
import network.platon.pid.sdk.req.pid.CreatePidReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.agency.SetAuthorityResp;
import network.platon.pid.sdk.resp.credential.CreateCredentialResp;
import network.platon.pid.sdk.resp.evidence.CreateEvidenceResp;
import network.platon.pid.sdk.resp.evidence.QueryEvidenceResp;
import network.platon.pid.sdk.resp.evidence.RevokeEvidenceResp;
import network.platon.pid.sdk.resp.pct.CreatePctResp;
import network.platon.pid.sdk.resp.pid.CreatePidResp;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TestEvidenceServiceImpl extends BaseTest {

	private static Logger logger = LoggerFactory.getLogger(TestEvidenceServiceImpl.class);

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
	public void test_createEvidence() throws Exception {
		// Create pid by privateKey
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String pid = testCreatePid(privateKey);

		// Create issuer's pid by privateKey.
		String issuerPriKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String issuer = testCreatePid(issuerPriKey);
		String issuerPubKeyId = issuer + "#keys-1";

		// Add an authority issuer
		testCreateAuth(issuer);

		// Create pct data in PlatON
		String pctId = testCreatePct(issuer, issuerPriKey, pctJson, type);

		// Create credential
		Credential credential = testCreateCredential(pctId, pid, issuerPriKey, issuerPubKeyId, issuer);

		CreateEvidenceReq req = CreateEvidenceReq.builder().credential(credential).privateKey(issuerPriKey).build();
		resp = PClient.createEvidenceClient(new InitContractData(issuerPriKey)).createEvidence(req);

		assertTrue(resp.checkSuccess());
	}

	@Test
	public void test_queryEvidence() throws Exception {
		// Create pid by privateKey
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String pid = testCreatePid(privateKey);

		// Create issuer's pid by privateKey.
		String issuerPriKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String issuer = testCreatePid(issuerPriKey);
		String issuerPubKeyId = issuer + "#keys-1";

		// Add an authority issuer
		testCreateAuth(issuer);

		// Create pct data in PlatON
		String pctId = testCreatePct(issuer, issuerPriKey, pctJson, type);

		// Create credential
		Credential credential = testCreateCredential(pctId, pid, issuerPriKey, issuerPubKeyId, issuer);

		String evidenceId = this.testCreateEvidence(credential, issuerPriKey);
		
		QueryEvidenceReq req = QueryEvidenceReq.builder().evidenceId(evidenceId).build();
		resp = evidenceService.queryEvidence(req);

		assertTrue(resp.checkSuccess());
	}

	@Test
	public void test_verifyEvidence() throws Exception {
		// Create pid by privateKey
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String pid = testCreatePid(privateKey);

		// Create issuer's pid by privateKey.
		String issuerPriKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String issuer = testCreatePid(issuerPriKey);
		String issuerPubKeyId = issuer + "#keys-1";

		// Add an authority issuer
		testCreateAuth(issuer);

		// Create pct data in PlatON
		String pctId = testCreatePct(issuer, issuerPriKey, pctJson, type);

		// Create credential
		Credential credential = testCreateCredential(pctId, pid, issuerPriKey, issuerPubKeyId, issuer);

		// Create evidence
		String evidenceId = testCreateEvidence(credential, issuerPriKey);

		// Query evidence by id
		QueryEvidenceResp queryEvidenceResp = testQueryEvidence(evidenceId);

		// success
		VerifyEvidenceReq req = VerifyEvidenceReq.builder().credentialHash(credential.obtainAllHash())
				.evidenceSignInfo(queryEvidenceResp.getSignInfo()).publicKeyId(issuerPubKeyId).build();
		resp = PClient.createEvidenceClient().verifyEvidence(req);

		assertTrue(resp.checkSuccess());
	}

	@Test
	public void test_revokeEvidence() throws Exception {
		// Create pid by privateKey
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String pid = testCreatePid(privateKey);

		// Create issuer's pid by privateKey.
		String issuerPriKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String issuer = testCreatePid(issuerPriKey);
		String issuerPubKeyId = issuer + "#keys-1";

		// Add an authority issuer
		testCreateAuth(issuer);

		// Create pct data in PlatON
		String pctId = testCreatePct(issuer, issuerPriKey, pctJson, type);

		// Create credential
		Credential credential = testCreateCredential(pctId, pid, issuerPriKey, issuerPubKeyId, issuer);

		// Create evidence
		String evidenceId = testCreateEvidence(credential, issuerPriKey);

		RevokeEvidenceReq req = RevokeEvidenceReq.builder().credential(credential)
				.privateKey(issuerPriKey).build();
		resp = PClient.createEvidenceClient(new InitContractData(issuerPriKey)).revokeEvidence(req);
		assertTrue(resp.checkSuccess());

		VerifyCredentialEvidenceReq verifyEvidenceReq = VerifyCredentialEvidenceReq.builder().credential(credential)
				.build();
		resp = PClient.createEvidenceClient(new InitContractData(issuerPriKey)).verifyCredentialEvidence(verifyEvidenceReq);
	}

	@Test
	public void test_verifyCredentialEvidence() throws Exception {
		// Create pid by privateKey
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String pid = testCreatePid(privateKey);

		// Create issuer's pid by privateKey.
		String issuerPriKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String issuer = testCreatePid(issuerPriKey);
		String issuerPubKeyId = issuer + "#keys-1";

		// Add an authority issuer
		testCreateAuth(issuer);

		// Create pct data in PlatON
		String pctId = testCreatePct(issuer, issuerPriKey, pctJson, type);

		// Create credential
		Credential credential = testCreateCredential(pctId, pid, issuerPriKey, issuerPubKeyId, issuer);

		// String evidenceId =
		// testCreateEvidence(credential, issuerPriKey);

		// The evidence is not exists
		VerifyCredentialEvidenceReq req = VerifyCredentialEvidenceReq.builder().credential(credential).build();
		resp = PClient.createEvidenceClient().verifyCredentialEvidence(req);

		// The credential is null
		req = VerifyCredentialEvidenceReq.builder().credential(null).build();
		resp = PClient.createEvidenceClient().verifyCredentialEvidence(req);

		// Create evidence
		testCreateEvidence(credential, issuerPriKey);

		// susccess
		req = VerifyCredentialEvidenceReq.builder().credential(credential).build();
		resp = PClient.createEvidenceClient().verifyCredentialEvidence(req);
	}

	private String testCreateEvidence(Credential credential, String issuerPriKey) throws Exception {
		CreateEvidenceReq req = CreateEvidenceReq.builder().credential(credential).privateKey(issuerPriKey).build();
		BaseResp<CreateEvidenceResp> createEvidenceBaseResp = PClient.createEvidenceClient(new InitContractData(issuerPriKey)).createEvidence(req);

		if (createEvidenceBaseResp.checkFail()) {
			String msg = JSONObject.toJSONString(createEvidenceBaseResp);

			throw new Exception(msg);
		}
		return createEvidenceBaseResp.getData().getEvidenceId();
	}

	private Boolean testRevokeEvidence(Credential credential, String issuerPriKey) throws Exception {
		RevokeEvidenceReq req = RevokeEvidenceReq.builder().credential(credential)
				.privateKey(issuerPriKey).build();
		BaseResp<RevokeEvidenceResp> revokeEvidence = PClient.createEvidenceClient(new InitContractData(issuerPriKey)).revokeEvidence(req);

		if (revokeEvidence.checkFail()) {
			String msg = JSONObject.toJSONString(revokeEvidence);

			throw new Exception(msg);
		}
		return revokeEvidence.getData().isStatus();
	}

	private QueryEvidenceResp testQueryEvidence(String evidenceId) throws Exception {
		QueryEvidenceReq req = QueryEvidenceReq.builder().evidenceId(evidenceId).build();
		BaseResp<QueryEvidenceResp> queryEvidenceResp = PClient.createEvidenceClient().queryEvidence(req);
		if (queryEvidenceResp.checkFail()) {
			String msg = JSONObject.toJSONString(queryEvidenceResp);

			throw new Exception(msg);
		}

		return queryEvidenceResp.getData();
	}

	private Credential testCreateCredential(String pctId, String pid, String issuerPriKey, String issuerPubKeyId, String issuer) {
		try {
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
			BaseResp<CreateCredentialResp> resp = PClient.createCredentialClient().createCredential(req);
			if (resp.checkSuccess())
				return resp.getData().getCredential();
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
