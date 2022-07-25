package network.platon.pid.sdk.service.impl;

import com.platon.crypto.ECKeyPair;
import com.platon.crypto.Keys;
import com.platon.utils.Numeric;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import network.platon.pid.sdk.base.dto.PidService;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.req.pid.*;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.pid.QueryPidDocumentDataResp;
import network.platon.pid.sdk.utils.PidUtils;
import org.junit.Test;
import network.platon.pid.sdk.BaseTest;
import network.platon.pid.sdk.service.PidentityService;

import static network.platon.pid.sdk.constant.PidConst.PID_EVENT_ATTRIBUTE_CHANGE_TOPIC;


@Slf4j
public class TestPidServiceImpl extends BaseTest{

	private PidentityService pidService = new PidentityServiceImpl();


	@Data
	private class createPidResult{
		private String privateKey;
		private String publicKey;
		private String pid;
	}

	private createPidResult createPid() {
		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return null;
		}
		String privateKey = Numeric.toHexStringWithPrefix(keyPair.getPrivateKey());
		String publicKey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());
		String pid = PidUtils.generatePid(publicKey);

		if (!createIdentityByPrivateKey(resp, privateKey)) {
			return null;
		}

		createPidResult result = new createPidResult();
		result.setPrivateKey(privateKey);
		result.setPublicKey(publicKey);
		result.setPid(pid);
		return result;
	}

	@Test
	public void test_createPidByPrivateKey() {
		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}
	}


	@Test
	public void test_createPidWithInvalidPrivateKey() {

		// 0xee7881281ef73ae93b844575270264990c2b1a339203fde1ba579bf56794d7be
		String invalidPrivateKey = "0xee7881281ef73ae93b844575270264990c2b1a339203fde1ba579bf56794d7be1w";
		CreatePidReq req = CreatePidReq.builder().privateKey(invalidPrivateKey).build();
		failedResult(pidService.createPid(req));
	}


	@Test
	public void test_createPidRepeat() {
		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		CreatePidReq req = CreatePidReq.builder().privateKey(result.getPrivateKey()).build();
		failedResult(pidService.createPid(req));
	}


	@Test
	public void test_queryPidDocument() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(result.getPid()).build();
		okResult(pidService.queryPidDocument(queryPidDocumentReq));
	}


	@Test
	public void test_queryPidDocumentData() {
		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(result.getPid()).build();
		BaseResp<QueryPidDocumentDataResp> resp =  pidService.queryPidDocumentData(queryPidDocumentReq);

		okResult(resp);
	}

	@Test
	public void test_queryPidDocumentEmpty() {
		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}

		String privateKey = Numeric.toHexStringWithPrefix(keyPair.getPrivateKey());
		ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.hexStringToByteArray(privateKey));
		String publicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String pid = PidUtils.generatePid(publicKey);
		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(pid).build();
		failedResult(pidService.queryPidDocument(queryPidDocumentReq));
	}


	@Test
	public void test_addPublicKey() {


		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		ECKeyPair keyPair = null;
		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());
		String publicKey1 = Numeric.toHexStringWithPrefix( AlgorithmHandler.createEcKeyPair(result.getPrivateKey()).getPublicKey());

		// duplicate public key
		AddPublicKeyReq req= AddPublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey1)
				.type(PidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		failedResult(pidService.addPublicKey(req));

		// duplicate index
		req= AddPublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.SECP256K1)
				.index(1)
				.build();
		failedResult(pidService.addPublicKey(req));


		req= AddPublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		okResult(pidService.addPublicKey(req));


		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(result.getPid()).build();
		BaseResp<QueryPidDocumentDataResp> resp =  pidService.queryPidDocumentData(queryPidDocumentReq);
		okResult(resp);
	}

	@Test
	public void test_addPublicKeyRepeat() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		AddPublicKeyReq req= AddPublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.SECP256K1)
				.build();
		okResult(pidService.addPublicKey(req));
		failedResult(pidService.addPublicKey(req));
	}


	@Test
	public void test_addPublicKeyByRevocation() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		AddPublicKeyReq req= AddPublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		okResult(pidService.addPublicKey(req));

		// revocation publicKey
		RevocationPublicKeyReq revocationPublicKeyReq = RevocationPublicKeyReq.builder()
				.privateKey(req.getPrivateKey())
				.publicKey(publicKey2)
				.build();

		okResult(pidService.revocationPublicKey(revocationPublicKeyReq));

		// reset the revocation publicKey
		failedResult(pidService.addPublicKey(req));
	}


	@Test
	public void test_addPublicKeyToInvalidDoc() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		// revocation document
		ChangeDocumentStatusReq changeDocumentStatusReq = ChangeDocumentStatusReq.builder()
				.privateKey(result.getPrivateKey())
				.status(PidConst.DocumentStatus.DEACTIVATION)
				.build();

		okResult(pidService.changeDocumentStatus(changeDocumentStatusReq));

		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		AddPublicKeyReq req= AddPublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.SECP256K1)
				.index(1)
				.build();

		// add publicKey to revocation document
		failedResult(pidService.addPublicKey(req));
	}


	@Test
	public void test_updatePublicKey() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		ECKeyPair keyPair = null;


		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		UpdatePublicKeyReq req= UpdatePublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.RSA)
				.index(1)
				.build();
		okResult(pidService.updatePublicKey(req));

		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(result.getPid()).build();
		BaseResp<QueryPidDocumentDataResp> resp =  pidService.queryPidDocumentData(queryPidDocumentReq);
		okResult(resp);
	}


	@Test
	public void test_updatePublicKeyWithInvalid() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		UpdatePublicKeyReq req= UpdatePublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(result.getPublicKey())
				.type(PidConst.PublicKeyType.RSA)
				.index(1)
				.build();
		failedResult(pidService.updatePublicKey(req));

		req= UpdatePublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.RSA)
				.index(1)
				.build();

		okResult(pidService.updatePublicKey(req));

		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(result.getPid()).build();
		BaseResp<QueryPidDocumentDataResp> resp =  pidService.queryPidDocumentData(queryPidDocumentReq);
		okResult(resp);
	}


	@Test
	public void test_updateNoExistPublicKey() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		// update the not exist public Key
		UpdatePublicKeyReq req= UpdatePublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.RSA)
				.index(2)
				.build();
		failedResult(pidService.updatePublicKey(req));
	}


	@Test
	public void test_updatePublicKeyByRevocation() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		AddPublicKeyReq req= AddPublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		okResult(pidService.addPublicKey(req));

		// revocation publicKey
		RevocationPublicKeyReq revocationPublicKeyReq = RevocationPublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.build();

		okResult(pidService.revocationPublicKey(revocationPublicKeyReq));

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey3 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		UpdatePublicKeyReq updatePublicKeyReq = UpdatePublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey3)
				.type(PidConst.PublicKeyType.RSA)
				.index(2)
				.build();
		failedResult(pidService.updatePublicKey(updatePublicKeyReq));

		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(result.getPid()).build();
		BaseResp<QueryPidDocumentDataResp> resp =  pidService.queryPidDocumentData(queryPidDocumentReq);
		okResult(resp);
	}

	@Test
	public void test_updatePublicKeyToInvalidDoc() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);

		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		AddPublicKeyReq req= AddPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		// add the publicKey
		okResult(pidService.addPublicKey(req));

		// revocation document
		ChangeDocumentStatusReq changeDocumentStatusReq = ChangeDocumentStatusReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentStatus.DEACTIVATION)
				.build();

		okResult(pidService.changeDocumentStatus(changeDocumentStatusReq));

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey3 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		UpdatePublicKeyReq updatePublicKeyReq = UpdatePublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey3)
				.type(PidConst.PublicKeyType.RSA)
				.index(2)
				.build();
		failedResult(pidService.updatePublicKey(updatePublicKeyReq));

		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(result.getPid()).build();
		BaseResp<QueryPidDocumentDataResp> resp =  pidService.queryPidDocumentData(queryPidDocumentReq);
		okResult(resp);
	}

	@Test
	public void test_revocationPublicKey() {
		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);

		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		// add a public Key
		AddPublicKeyReq addPublicKeyReq= AddPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		okResult(pidService.addPublicKey(addPublicKeyReq));

		// revacation the public key
		RevocationPublicKeyReq revocationPublicKeyReq= RevocationPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey2)
				.build();
		okResult(pidService.revocationPublicKey(revocationPublicKeyReq));

		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(result.getPid()).build();
		BaseResp<QueryPidDocumentDataResp> resp =  pidService.queryPidDocumentData(queryPidDocumentReq);
		okResult(resp);
	}


	@Test
	public void test_revocationLastPublicKey() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);

		// revacation the last public key
		RevocationPublicKeyReq revocationPublicKeyReq= RevocationPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey)
				.build();
		failedResult(pidService.revocationPublicKey(revocationPublicKeyReq));

		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(result.getPid()).build();
		BaseResp<QueryPidDocumentDataResp> resp =  pidService.queryPidDocumentData(queryPidDocumentReq);
		okResult(resp);
	}

	@Test
	public void test_revocationNoExistPublicKey() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);

		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		// revacation the not exist public key
		RevocationPublicKeyReq revocationPublicKeyReq= RevocationPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey2)
				.build();
		failedResult(pidService.revocationPublicKey(revocationPublicKeyReq));

		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(result.getPid()).build();
		BaseResp<QueryPidDocumentDataResp> resp =  pidService.queryPidDocumentData(queryPidDocumentReq);
		okResult(resp);
	}

	@Test
	public void test_revocationPublicKeyRepeat() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);

		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		// add a public Key
		AddPublicKeyReq addPublicKeyReq= AddPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		okResult(pidService.addPublicKey(addPublicKeyReq));

		// revacation the public key
		RevocationPublicKeyReq revocationPublicKeyReq= RevocationPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey2)
				.build();
		okResult(pidService.revocationPublicKey(revocationPublicKeyReq));
		failedResult(pidService.revocationPublicKey(revocationPublicKeyReq));

		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(result.getPid()).build();
		BaseResp<QueryPidDocumentDataResp> resp =  pidService.queryPidDocumentData(queryPidDocumentReq);
		okResult(resp);
	}


	@Test
	public void test_revocationPublicKeyToInvalidDoc() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);

		ECKeyPair keyPair = null;
		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}
		String publicKey2 = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		// add a public Key
		AddPublicKeyReq addPublicKeyReq= AddPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey2)
				.type(PidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		okResult(pidService.addPublicKey(addPublicKeyReq));


		// revocation document
		ChangeDocumentStatusReq changeDocumentStatusReq = ChangeDocumentStatusReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentStatus.DEACTIVATION)
				.build();

		okResult(pidService.changeDocumentStatus(changeDocumentStatusReq));

		// revacation the public key
		RevocationPublicKeyReq revocationPublicKeyReq= RevocationPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey2)
				.build();
		failedResult(pidService.revocationPublicKey(revocationPublicKeyReq));

		QueryPidDocumentReq queryPidDocumentReq = QueryPidDocumentReq.builder().pid(result.getPid()).build();
		BaseResp<QueryPidDocumentDataResp> resp =  pidService.queryPidDocumentData(queryPidDocumentReq);
		okResult(resp);
	}

	@Test
	public void test_setService() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);


		PidService svr = new PidService();
		// serviceId: did:pid:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service
		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://PIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentAttrStatus.PID_SERVICE_VALID)
				.service(svr)
				.build();
		okResult(pidService.setService(req));
	}

	@Test
	public void test_setServiceWithInvalid() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);


		PidService svr = new PidService();
		// serviceId: did:pid:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service
		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://PIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentAttrStatus.PID_SERVICE_INVALID)
				.service(svr)
				.build();
		failedResult(pidService.setService(req));
	}


	@Test
	public void test_setServiceToInvalidDoc() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);


		// revocation document
		ChangeDocumentStatusReq changeDocumentStatusReq = ChangeDocumentStatusReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentStatus.DEACTIVATION)
				.build();

		okResult(pidService.changeDocumentStatus(changeDocumentStatusReq));


		PidService svr = new PidService();
		// serviceId: did:pid:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service
		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://PIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentAttrStatus.PID_SERVICE_INVALID)
				.service(svr)
				.build();
		failedResult(pidService.setService(req));
	}


	@Test
	public void test_revocationService() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);


		PidService svr = new PidService();
		// serviceId: did:pid:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service
		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://PIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentAttrStatus.PID_SERVICE_VALID)
				.service(svr)
				.build();

		// add the service
		okResult(pidService.setService(req));

		// revocation the service
		okResult(pidService.revocationService(req));
	}


	@Test
	public void test_revocationServiceRepeat() {
		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);


		PidService svr = new PidService();
		// serviceId: did:pid:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service
		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://PIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentAttrStatus.PID_SERVICE_VALID)
				.service(svr)
				.build();

		// add the service
		okResult(pidService.setService(req));

		// revocation the service
		okResult(pidService.revocationService(req));
		failedResult(pidService.revocationService(req));
	}

	@Test
	public void test_revocationNoExistService() {
		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);


		PidService svr = new PidService();
		// serviceId: did:pid:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service
		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://PIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentAttrStatus.PID_SERVICE_VALID)
				.service(svr)
				.build();

		// revocation the service
		failedResult(pidService.revocationService(req));
	}

	@Test
	public void test_revocationServiceToInvalidDoc() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);


		PidService svr = new PidService();
		// serviceId: did:pid:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service
		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://PIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentAttrStatus.PID_SERVICE_VALID)
				.service(svr)
				.build();

		// add the service
		okResult(pidService.setService(req));


		// revocation document
		ChangeDocumentStatusReq changeDocumentStatusReq = ChangeDocumentStatusReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentStatus.DEACTIVATION)
				.build();

		okResult(pidService.changeDocumentStatus(changeDocumentStatusReq));

		// revocation the service
		failedResult(pidService.revocationService(req));
	}

	@Test
	public void test_isPidExist() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);

		okResult(pidService.isPidExist(pid));
	}

	@Test
	public void test_getDocumentStatus() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}

		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);


		okResult(pidService.getDocumentStatus(pid));
	}

	@Test
	public void test_changeDocumentStatus() {

		createPidResult result = this.createPid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String pid = PidUtils.generatePid(publicKey);


		ChangeDocumentStatusReq req = ChangeDocumentStatusReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentStatus.ACTIVATION)
				.build();

		okResult(pidService.changeDocumentStatus(req));
	}


	@Test
	public void test_changeNoExistDocumentStatus() {

		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return;
		}

		String  privateKey = Numeric.toHexStringWithPrefix(keyPair.getPrivateKey());
		String publicKey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());

		ChangeDocumentStatusReq req = ChangeDocumentStatusReq.builder()
				.privateKey(privateKey)
				.status(PidConst.DocumentStatus.ACTIVATION)
				.build();

		failedResult(pidService.changeDocumentStatus(req));
	}
}
