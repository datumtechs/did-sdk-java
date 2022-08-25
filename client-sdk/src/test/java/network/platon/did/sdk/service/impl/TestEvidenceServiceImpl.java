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
import network.platon.did.contract.dto.InitContractData;
import network.platon.did.csies.algorithm.AlgorithmHandler;
import network.platon.did.sdk.BaseTest;
import network.platon.did.sdk.base.dto.Credential;
import network.platon.did.sdk.factory.PClient;
import network.platon.did.sdk.req.credential.CreateCredentialReq;
import network.platon.did.sdk.req.evidence.*;
import network.platon.did.sdk.req.pct.CreatePctReq;
import network.platon.did.sdk.req.did.CreateDidReq;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.credential.CreateCredentialResp;
import network.platon.did.sdk.resp.evidence.CreateEvidenceResp;
import network.platon.did.sdk.resp.evidence.QueryEvidenceResp;
import network.platon.did.sdk.resp.evidence.RevokeEvidenceResp;
import network.platon.did.sdk.resp.pct.CreatePctResp;
import network.platon.did.sdk.resp.did.CreateDidResp;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
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
	public void test_nodeCredential(){
		String credentialStr = "{\"@context\":\"https://www.w3.org/2018/credentials/v1\",\"version\":\"1.0.0\",\"id\":\"93602e24-e871-40bd-ba5f-213fad05db92\",\"type\":[\"VerifiableCredential\"],\"issuer\":\"did:pid:lat1g83xnwcqc4uufpx9588muq0e98g93rlmpgx563\",\"issuanceDate\":\"2022-08-23T10:07:16.045\",\"expirationDate\":\"2122-08-23T10:07:15.695\",\"claimData\":{\"nodeId\":\"did:pid:lat16wyuj8qqefqlph50mr5ykv246qvg3lyl9zm3vm\",\"nodeName\":\"org156org156\",\"url\":\"ipfs://QmSWSCyDxa8Be1VG4TfeyCTkXt1AZh6hjaTbKTzm2jnMQG\"},\"claimMeta\":{\"pctId\":\"1000\"},\"proof\":{\"claimRootHash\":\"0x1e6b4629c38d4a17b7a7bc81b15bebd3e73291fcdb99f190460ca50f5b70ca39\",\"created\":\"2022-08-23T10:07:16.045\",\"jws\":\"0x8d8949ec13cb55e5277a1a9749bc02f30fa46cfaaf0729527ac54132912027c86e5816ebbb79ab45bbefa643c8350f3bea65c1467d583dd73d15165af8aa100001\",\"seed\":\"11172146488962401086\",\"type\":\"Secp256k1\",\"verificationMethod\":\"did:pid:lat1g83xnwcqc4uufpx9588muq0e98g93rlmpgx563#keys-1\"},\"holder\":\"did:pid:lat16wyuj8qqefqlph50mr5ykv246qvg3lyl9zm3vm\"}";
		Credential credential = JSONObject.parseObject(credentialStr, Credential.class);
		VerifyCredentialEvidenceReq credentialReq = VerifyCredentialEvidenceReq.builder().credential(credential).build();
		BaseResp<String> result = PClient.createEvidenceClient().verifyCredentialEvidence(credentialReq);
		assertTrue(result.checkSuccess());
		System.out.println(result);
	}

	@Test
	public void test_createEvidence() throws Exception {
		// Create did by privateKey
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String did = testCreateDid(privateKey);

		// Create issuer's did by privateKey.
		String issuerPriKey = DidConfig.getCONTRACT_PRIVATEKEY();
		String issuer = testCreateDid(issuerPriKey);
		String issuerPubKeyId = issuer + "#keys-1";

		// Create pct data in PlatON
		String pctId = testCreatePct(pctJson);

		// Create credential
		Credential credential = testCreateCredential(pctId, did, issuerPriKey, issuerPubKeyId, issuer);

		CreateEvidenceReq req = CreateEvidenceReq.builder().credential(credential).privateKey(issuerPriKey).build();
		resp = PClient.createEvidenceClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY())).createEvidence(req);

		assertTrue(resp.checkSuccess());
	}

	@Test
	public void test_queryEvidence() throws Exception {
		// Create did by privateKey
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String did = testCreateDid(privateKey);

		// Create issuer's did by privateKey.
		String issuerPriKey = DidConfig.getCONTRACT_PRIVATEKEY();
		String issuer = testCreateDid(issuerPriKey);
		String issuerPubKeyId = issuer + "#keys-1";

		// Create pct data in PlatON
		String pctId = testCreatePct(pctJson);

		// Create credential
		Credential credential = testCreateCredential(pctId, did, issuerPriKey, issuerPubKeyId, issuer);

		String evidenceId = this.testCreateEvidence(credential, issuerPriKey);
		
		QueryEvidenceReq req = QueryEvidenceReq.builder().evidenceId(evidenceId).build();
		resp = evidenceService.queryEvidence(req);

		assertTrue(resp.checkSuccess());
	}


	@Test
	public void test_revokeEvidence() throws Exception {
		// Create did by privateKey
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String did = testCreateDid(privateKey);

		// Create issuer's did by privateKey.
		String issuerPriKey = DidConfig.getCONTRACT_PRIVATEKEY();
		String issuer = testCreateDid(issuerPriKey);

		String issuerPubKeyId = issuer + "#keys-1";

		// Create pct data in PlatON
		String pctId = testCreatePct(pctJson);

		// Create credential
		Credential credential = testCreateCredential(pctId, did, issuerPriKey, issuerPubKeyId, issuer);

		// Create evidence
		String evidenceId = testCreateEvidence(credential, issuerPriKey);

		RevokeEvidenceReq req = RevokeEvidenceReq.builder().evidenceId(credential.obtainHash())
				.privateKey(issuerPriKey).build();
		resp = PClient.createEvidenceClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY())).revokeEvidence(req);
		assertTrue(resp.checkSuccess());

		VerifyCredentialEvidenceReq verifyEvidenceReq = VerifyCredentialEvidenceReq.builder().credential(credential)
				.build();
		resp = PClient.createEvidenceClient(new InitContractData(issuerPriKey)).verifyCredentialEvidence(verifyEvidenceReq);
		assertTrue(resp.checkFail());
	}

	@Test
	public void test_verifyCredentialEvidence() throws Exception {
		// Create did by privateKey
		String privateKey = Keys.createEcKeyPair().getPrivateKey().toString(16);
		String did = testCreateDid(privateKey);

		// Create issuer's did by privateKey.
		String issuerPriKey = DidConfig.getCONTRACT_PRIVATEKEY();
		String issuer = testCreateDid(issuerPriKey);
		String issuerPubKeyId = issuer + "#keys-1";

		// Create pct data in PlatON
		String pctId = testCreatePct(pctJson);

		// Create credential
		Credential credential = testCreateCredential(pctId, did, issuerPriKey, issuerPubKeyId, issuer);

		// The evidence is not exists
		VerifyCredentialEvidenceReq req = VerifyCredentialEvidenceReq.builder().credential(credential).build();
		resp = PClient.createEvidenceClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY())).verifyCredentialEvidence(req);
		assertTrue(resp.checkFail());

		// The credential is null
		req = VerifyCredentialEvidenceReq.builder().credential(null).build();
		resp = PClient.createEvidenceClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY())).verifyCredentialEvidence(req);
		assertTrue(resp.checkFail());

		// Create evidence
		testCreateEvidence(credential, issuerPriKey);

		// susccess
		req = VerifyCredentialEvidenceReq.builder().credential(credential).build();
		resp = PClient.createEvidenceClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY())).verifyCredentialEvidence(req);
		assertTrue(resp.checkSuccess());
	}

	private String testCreateEvidence(Credential credential, String issuerPriKey) throws Exception {
		CreateEvidenceReq req = CreateEvidenceReq.builder().credential(credential).privateKey(issuerPriKey).build();
		BaseResp<CreateEvidenceResp> createEvidenceBaseResp = PClient.createEvidenceClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY())).createEvidence(req);

		if (createEvidenceBaseResp.checkFail()) {
			String msg = JSONObject.toJSONString(createEvidenceBaseResp);

			throw new Exception(msg);
		}
		return createEvidenceBaseResp.getData().getEvidenceId();
	}

	private Boolean testRevokeEvidence(Credential credential, String issuerPriKey) throws Exception {
		RevokeEvidenceReq req = RevokeEvidenceReq.builder().evidenceId(credential.obtainHash())
				.privateKey(issuerPriKey).build();
		BaseResp<RevokeEvidenceResp> revokeEvidence = PClient.createEvidenceClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY())).revokeEvidence(req);

		if (revokeEvidence.checkFail()) {
			String msg = JSONObject.toJSONString(revokeEvidence);

			throw new Exception(msg);
		}
		return revokeEvidence.getData().isStatus();
	}

	private QueryEvidenceResp testQueryEvidence(String evidenceId) throws Exception {
		QueryEvidenceReq req = QueryEvidenceReq.builder().evidenceId(evidenceId).build();
		BaseResp<QueryEvidenceResp> queryEvidenceResp = PClient.createEvidenceClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY())).queryEvidence(req);
		if (queryEvidenceResp.checkFail()) {
			String msg = JSONObject.toJSONString(queryEvidenceResp);

			throw new Exception(msg);
		}

		return queryEvidenceResp.getData();
	}

	private Credential testCreateCredential(String pctId, String did, String issuerPriKey, String issuerPubKeyId, String issuer) {
		try {
			Map<String, Object> claim = new HashMap<>();
			claim.put("name", "zhangsan");
			claim.put("no", "123");
			claim.put("data", "456");

			String context = "https://platon.network/";
			long expirationDate = DateUtils.getCurrentTimeStamp() + 1000000000;
			String credentialType = "VerifiableCredential";

			CreateCredentialReq req = CreateCredentialReq.builder().claim(claim).context(context).expirationDate(expirationDate)
					.pctId(pctId).did(did).privateKey(issuerPriKey).publicKeyId(issuerPubKeyId).issuer(issuer)
					.type(credentialType).build();
			BaseResp<CreateCredentialResp> resp = PClient.createCredentialClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY())).createCredential(req);
			if (resp.checkSuccess())
				return resp.getData().getCredential();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
						BigDecimal.valueOf(100), Convert.Unit.PVON)
				.send();
		if(!receipt.isStatusOK()){
			String msg = "Create did error";
			logger.error("Create did error");

			throw new Exception(msg);
		}

		CreateDidReq req = CreateDidReq.builder().privateKey(privateKey).publicKey(publicKey).build();
		BaseResp<CreateDidResp> createDidResp = PClient.createDidentityClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY())).createDid(req);
		if (createDidResp.checkFail()) {
			String msg = JSONObject.toJSONString(createDidResp);
			logger.error("Create did error,error msg:{}", msg);

			throw new Exception(msg);
		}
		return createDidResp.getData().getDid();
	}

	private String testCreatePct(String pctJson) throws Exception {

		String str = "This is a String";
		CreatePctReq req = CreatePctReq.builder().pctjson(pctJson).privateKey(DidConfig.getCONTRACT_PRIVATEKEY()).extra(str.getBytes()).build();
		BaseResp<CreatePctResp> createPctBaseResp = PClient.createPctClient(new InitContractData(DidConfig.getCONTRACT_PRIVATEKEY())).registerPct(req);
		if (createPctBaseResp.checkFail()) {
			String msg = JSONObject.toJSONString(createPctBaseResp);
			logger.error("Register pct error,error msg:{}", msg);

			throw new Exception(msg);
		}
		return createPctBaseResp.getData().getPctId();
	}
}
