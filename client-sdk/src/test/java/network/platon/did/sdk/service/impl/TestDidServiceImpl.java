package network.platon.did.sdk.service.impl;

import com.platon.crypto.ECKeyPair;
import com.platon.crypto.Keys;
import com.platon.utils.Numeric;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import network.platon.did.csies.algorithm.AlgorithmHandler;
import network.platon.did.sdk.BaseTest;
import network.platon.did.sdk.base.dto.DidService;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.req.did.*;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.did.QueryDidDocumentDataResp;
import network.platon.did.sdk.resp.did.QueryDidDocumentResp;
import network.platon.did.sdk.service.DidentityService;
import network.platon.did.sdk.utils.DidUtils;
import org.junit.Test;


@Slf4j
public class TestDidServiceImpl extends BaseTest{

	private DidentityService didService = new DidentityServiceImpl();

	@Data
	private class createDidResult{
		private String privateKey;
		private String publicKey;
		private String did;
	}

	private createDidResult createDid() {
		ECKeyPair keyPair = null;

		try {
			keyPair = Keys.createEcKeyPair();
		} catch (Exception e) {
			log.error("Failed to create EcKeyPair, exception: {}", e);
			return null;
		}
		String privateKey = Numeric.toHexStringWithPrefix(keyPair.getPrivateKey());
		String publicKey = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());
		String did = DidUtils.generateDid(publicKey);

		if (!createIdentityByPrivateKey(resp, privateKey)) {
			return null;
		}

		createDidResult result = new createDidResult();
		result.setPrivateKey(privateKey);
		result.setPublicKey(publicKey);
		result.setDid(did);
		return result;
	}

	@Test
	public void test_createDidByPrivateKey() {
		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}
	}


	@Test
	public void test_createDidWithInvalidPrivateKey() {

		// 0xee7881281ef73ae93b844575270264990c2b1a339203fde1ba579bf56794d7be
		String invalidPrivateKey = "0xee7881281ef73ae93b844575270264990c2b1a339203fde1ba579bf56794d7be1w";
		CreateDidReq req = CreateDidReq.builder().privateKey(invalidPrivateKey).build();
		failedResult(didService.createDid(req));
	}


	@Test
	public void test_createDidRepeat() {
		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}

		CreateDidReq req = CreateDidReq.builder().privateKey(result.getPrivateKey()).build();
		failedResult(didService.createDid(req));
	}


	@Test
	public void test_queryDidDocument() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		okResult(didService.queryDidDocument(queryDidDocumentReq));
	}


	@Test
	public void test_queryDidDocumentData() {
		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);

		okResult(resp);
	}

	@Test
	public void test_queryDidDocumentEmpty() {
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
		String did = DidUtils.generateDid(publicKey);
		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(did).build();
		failedResult(didService.queryDidDocument(queryDidDocumentReq));
	}


	@Test
	public void test_addPublicKey() {


		createDidResult result = this.createDid();
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
				.type(DidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		failedResult(didService.addPublicKey(req));

		// duplicate index
		req= AddPublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.type(DidConst.PublicKeyType.SECP256K1)
				.index(1)
				.build();
		failedResult(didService.addPublicKey(req));


		req= AddPublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.type(DidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		okResult(didService.addPublicKey(req));


		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);
	}

	@Test
	public void test_addPublicKeyRepeat() {

		createDidResult result = this.createDid();
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
				.type(DidConst.PublicKeyType.SECP256K1)
				.build();
		okResult(didService.addPublicKey(req));
		failedResult(didService.addPublicKey(req));
	}


	@Test
	public void test_addPublicKeyByRevocation() {

		createDidResult result = this.createDid();
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
				.type(DidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		okResult(didService.addPublicKey(req));

		// revocation publicKey
		RevocationPublicKeyReq revocationPublicKeyReq = RevocationPublicKeyReq.builder()
				.privateKey(req.getPrivateKey())
				.publicKey(publicKey2)
				.build();

		okResult(didService.revocationPublicKey(revocationPublicKeyReq));

		// reset the revocation publicKey
		failedResult(didService.addPublicKey(req));
	}


	@Test
	public void test_addPublicKeyToInvalidDoc() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}

		// revocation document
		ChangeDocumentStatusReq changeDocumentStatusReq = ChangeDocumentStatusReq.builder()
				.privateKey(result.getPrivateKey())
				.status(DidConst.DocumentStatus.DEACTIVATION)
				.build();

		okResult(didService.changeDocumentStatus(changeDocumentStatusReq));

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
				.type(DidConst.PublicKeyType.SECP256K1)
				.index(1)
				.build();

		// add publicKey to revocation document
		failedResult(didService.addPublicKey(req));
	}


	@Test
	public void test_updatePublicKey() {

		createDidResult result = this.createDid();
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
				.type(DidConst.PublicKeyType.RSA)
				.index(1)
				.build();
		okResult(didService.updatePublicKey(req));

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);
	}


	@Test
	public void test_updatePublicKeyWithInvalid() {

		createDidResult result = this.createDid();
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
				.type(DidConst.PublicKeyType.RSA)
				.index(1)
				.build();
		failedResult(didService.updatePublicKey(req));

		req= UpdatePublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.type(DidConst.PublicKeyType.RSA)
				.index(1)
				.build();

		okResult(didService.updatePublicKey(req));

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);
	}


	@Test
	public void test_updateNoExistPublicKey() {

		createDidResult result = this.createDid();
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
				.type(DidConst.PublicKeyType.RSA)
				.index(2)
				.build();
		failedResult(didService.updatePublicKey(req));
	}


	@Test
	public void test_updatePublicKeyByRevocation() {

		createDidResult result = this.createDid();
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
				.type(DidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		okResult(didService.addPublicKey(req));

		// revocation publicKey
		RevocationPublicKeyReq revocationPublicKeyReq = RevocationPublicKeyReq.builder()
				.privateKey(result.getPrivateKey())
				.publicKey(publicKey2)
				.build();

		okResult(didService.revocationPublicKey(revocationPublicKeyReq));

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
				.type(DidConst.PublicKeyType.RSA)
				.index(2)
				.build();
		failedResult(didService.updatePublicKey(updatePublicKeyReq));

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);
	}

	@Test
	public void test_updatePublicKeyToInvalidDoc() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}

		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);

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
				.type(DidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		// add the publicKey
		okResult(didService.addPublicKey(req));

		// revocation document
		ChangeDocumentStatusReq changeDocumentStatusReq = ChangeDocumentStatusReq.builder()
				.privateKey(privateKey)
				.status(DidConst.DocumentStatus.DEACTIVATION)
				.build();

		okResult(didService.changeDocumentStatus(changeDocumentStatusReq));

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
				.type(DidConst.PublicKeyType.RSA)
				.index(2)
				.build();
		failedResult(didService.updatePublicKey(updatePublicKeyReq));

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);
	}

	@Test
	public void test_revocationPublicKey() {
		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}

		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);

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
				.type(DidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		okResult(didService.addPublicKey(addPublicKeyReq));

		// revacation the public key
		RevocationPublicKeyReq revocationPublicKeyReq= RevocationPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey2)
				.build();

		okResult(didService.revocationPublicKey(revocationPublicKeyReq));

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);

	}


	@Test
	public void test_revocationLastPublicKey() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);

		// revacation the last public key
		RevocationPublicKeyReq revocationPublicKeyReq= RevocationPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey)
				.build();

		failedResult(didService.revocationPublicKey(revocationPublicKeyReq));

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);

	}

	@Test
	public void test_revocationNoExistPublicKey() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);

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

		failedResult(didService.revocationPublicKey(revocationPublicKeyReq));

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);

	}

	@Test
	public void test_revocationPublicKeyRepeat() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);

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
				.type(DidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		okResult(didService.addPublicKey(addPublicKeyReq));

		// revacation the public key
		RevocationPublicKeyReq revocationPublicKeyReq= RevocationPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey2)
				.build();

		okResult(didService.revocationPublicKey(revocationPublicKeyReq));
		failedResult(didService.revocationPublicKey(revocationPublicKeyReq));

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);

	}


	@Test
	public void test_revocationPublicKeyToInvalidDoc() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);

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
				.type(DidConst.PublicKeyType.SECP256K1)
				.index(2)
				.build();
		okResult(didService.addPublicKey(addPublicKeyReq));


		// revocation document
		ChangeDocumentStatusReq changeDocumentStatusReq = ChangeDocumentStatusReq.builder()
				.privateKey(privateKey)
				.status(DidConst.DocumentStatus.DEACTIVATION)
				.build();

		okResult(didService.changeDocumentStatus(changeDocumentStatusReq));

		// revacation the public key
		RevocationPublicKeyReq revocationPublicKeyReq= RevocationPublicKeyReq.builder()
				.privateKey(privateKey)
				.publicKey(publicKey2)
				.build();

		failedResult(didService.revocationPublicKey(revocationPublicKeyReq));

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);

	}

	@Test
	public void test_setService() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);


		DidService svr = new DidService();
		// serviceId: did:did:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service
		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://DIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(DidConst.DocumentAttrStatus.DID_SERVICE_VALID)
				.service(svr)
				.build();

		okResult(didService.setService(req));

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);
	}

	@Test
	public void test_setServiceWithInvalid() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}

		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);

		DidService svr = new DidService();
		// serviceId: did:did:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service

		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://DIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(DidConst.DocumentAttrStatus.DID_SERVICE_INVALID)
				.service(svr)
				.build();

		failedResult(didService.setService(req));

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);

	}


	@Test
	public void test_setServiceToInvalidDoc() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}

		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);

		// revocation document
		ChangeDocumentStatusReq changeDocumentStatusReq = ChangeDocumentStatusReq.builder()
				.privateKey(privateKey)
				.status(DidConst.DocumentStatus.DEACTIVATION)
				.build();

		okResult(didService.changeDocumentStatus(changeDocumentStatusReq));

		DidService svr = new DidService();
		// serviceId: did:did:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service

		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://DIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(DidConst.DocumentAttrStatus.DID_SERVICE_INVALID)
				.service(svr)
				.build();
		failedResult(didService.setService(req));
	}


	@Test
	public void test_revocationService() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}

		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);

		DidService svr = new DidService();
		// serviceId: did:did:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service
		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://DIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(DidConst.DocumentAttrStatus.DID_SERVICE_VALID)
				.service(svr)
				.build();

		// add the service
		okResult(didService.setService(req));

		// revocation the service
		okResult(didService.revocationService(req));

		QueryDidDocumentReq queryDidDocumentReq = QueryDidDocumentReq.builder().did(result.getDid()).build();
		BaseResp<QueryDidDocumentResp> resp =  didService.queryDidDocument(queryDidDocumentReq);
		okResult(resp);

	}


	@Test
	public void test_revocationServiceRepeat() {
		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);


		DidService svr = new DidService();
		// serviceId: did:did:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service
		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://DIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(DidConst.DocumentAttrStatus.DID_SERVICE_VALID)
				.service(svr)
				.build();

		// add the service
		okResult(didService.setService(req));

		// revocation the service
		okResult(didService.revocationService(req));
		failedResult(didService.revocationService(req));
	}

	@Test
	public void test_revocationNoExistService() {
		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);


		DidService svr = new DidService();
		// serviceId: did:did:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service
		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://DIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(DidConst.DocumentAttrStatus.DID_SERVICE_VALID)
				.service(svr)
				.build();

		// revocation the service
		failedResult(didService.revocationService(req));
	}

	@Test
	public void test_revocationServiceToInvalidDoc() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);


		DidService svr = new DidService();
		// serviceId: did:did:lax1s4u4p9j95lh72a2c0ttj48ntd58s45resjgtza#some-service
		svr.setId("some-service");
		svr.setType("SomeServiceType");
		svr.setServiceEndpoint("https://DIdentity.platon.com/some-service/v1");
		SetServiceReq req= SetServiceReq.builder()
				.privateKey(privateKey)
				.status(DidConst.DocumentAttrStatus.DID_SERVICE_VALID)
				.service(svr)
				.build();

		// add the service
		okResult(didService.setService(req));


		// revocation document
		ChangeDocumentStatusReq changeDocumentStatusReq = ChangeDocumentStatusReq.builder()
				.privateKey(privateKey)
				.status(DidConst.DocumentStatus.DEACTIVATION)
				.build();

		okResult(didService.changeDocumentStatus(changeDocumentStatusReq));

		// revocation the service
		failedResult(didService.revocationService(req));
	}

	@Test
	public void test_isDidExist() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}

		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);

		okResult(didService.isDidExist(did));
	}

	@Test
	public void test_getDocumentStatus() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}

		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);


		okResult(didService.getDocumentStatus(did));
	}

	@Test
	public void test_changeDocumentStatus() {

		createDidResult result = this.createDid();
		if (null == result) {
			return;
		}


		String  privateKey = result.getPrivateKey();
		String publicKey = result.getPublicKey();
		String did = DidUtils.generateDid(publicKey);


		ChangeDocumentStatusReq req = ChangeDocumentStatusReq.builder()
				.privateKey(privateKey)
				.status(DidConst.DocumentStatus.ACTIVATION)
				.build();

		okResult(didService.changeDocumentStatus(req));
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
				.status(DidConst.DocumentStatus.ACTIVATION)
				.build();

		failedResult(didService.changeDocumentStatus(req));
	}
}
