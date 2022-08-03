package network.platon.did.sdk.service.impl;

import com.platon.crypto.ECKeyPair;
import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.contract.dto.InitContractData;
import network.platon.did.csies.algorithm.AlgorithmHandler;
import network.platon.did.sdk.base.dto.*;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.commonConstant;
import network.platon.did.sdk.contract.service.DidContractService;
import network.platon.did.sdk.req.did.*;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.TransactionResp;
import network.platon.did.sdk.resp.did.*;
import network.platon.did.sdk.service.BusinessBaseService;
import network.platon.did.sdk.service.DidentityService;
import network.platon.did.sdk.utils.DidUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

@Slf4j
public class DidentityServiceImpl extends BusinessBaseService implements DidentityService,Serializable,Cloneable {

	private static final long serialVersionUID = 5732175037207593062L;
	
	private static DidentityServiceImpl didentityServiceImpl = new DidentityServiceImpl();
	
	public static DidentityServiceImpl getInstance(){
        try {
            return (DidentityServiceImpl) didentityServiceImpl.clone();
        } catch (CloneNotSupportedException e) {
        	log.error("get instance error.", e);
        }
        return new DidentityServiceImpl();
    }

	public BaseResp<CreateDidResp> createDid(CreateDidReq req) {

		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData(), null);
		}

		if (!DidUtils.isPrivateKeyValid(req.getPrivateKey())) {
			log.error("Failed to call `createDid()`: the `privateKey` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String didPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String did = DidUtils.generateDid(didPublicKey);

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> tresp =
				this.getDidContractService()
						.createDid(did, req.getPublicKey(), req.getType());

		if (tresp.checkFail()) {
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
		CreateDidResp createDidResp = new CreateDidResp();
		createDidResp.setPrivateKey(req.getPrivateKey());
		createDidResp.setDid(did);
		createDidResp.setPublicKey(req.getPublicKey());
		createDidResp.setType(req.getType());
		createDidResp.setTransactionInfo(tresp.getTransactionInfo());
		return BaseResp.buildSuccess(createDidResp);
	}

	@Override
	public BaseResp<QueryDidDocumentResp> queryDidDocument(QueryDidDocumentReq req) {
		return null;
	}


	private BaseResp<QueryDidDocumentResp> queryDidDocument(String did) {

		if(!DidUtils.isValidDid(did)) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}
		BaseResp<DocumentData> getDocResp = this.getDidContractService().getDocument(DidUtils.convertDidToAddressStr(did));
		if (getDocResp.checkFail()) {
			return BaseResp.build(getDocResp.getCode(), getDocResp.getErrMsg());
		}
		// convert DocumentData to DocumentPojo
		Document doc = DidUtils.assembleDocumentPojo(getDocResp.getData());
		return BaseResp.buildSuccess(new QueryDidDocumentResp(doc));
	}

	@Override
	public BaseResp<SetDidAttrResp> addPublicKey(AddPublicKeyReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		req.setPublicKey(DidUtils.appendHexPrefix(req.getPublicKey()));

		if (!DidUtils.verifyAddPublicKeyArg(req)) {
			log.error("Failed to call `addPublicKey()`: the `AddPublickeyReq` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String didPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String did = DidUtils.generateDid(didPublicKey);

		String identity = DidUtils.convertDidToAddressStr(did);
		BaseResp<DocumentData> resp = this.getDidContractService().getDocument(identity);
		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}
		DocumentData doc = resp.getData();
		if (StringUtils.equals(DidConst.DocumentStatus.DEACTIVATION.getTag(), doc.getStatus())) {
			log.error(
					"Failed to call `addPublicKey()`: the identity has bean revocated, did: {}, status: {}",
					did, doc.getStatus()
			);
			return BaseResp.build(RetEnum.RET_DID_IDENTITY_ALREADY_REVOCATED);
		}
		int index = req.getIndex();
		boolean isExist = false;
		for (DocumentPubKeyData pubKey : doc.getPublicKey()) {
			String[] valueArray = StringUtils.splitByWholeSeparator(pubKey.getId(), commonConstant.SEPARATOR_DOCUMENT_PUBLICKEY_ID);
			if(index == Integer.parseInt(valueArray[1])){
				isExist = true;
				break;
			}

			if (StringUtils.equals(pubKey.getPublicKeyHex(), req.getPublicKey())) {
				isExist = true;
				break;
			}
		}
		if (isExist) {
			log.error(
					"Failed to call `addPublicKey()`: the public key is already exist, index: {}",
					index
			);
			return BaseResp.build(RetEnum.RET_DID_PUBLICKEY_ALREADY_EXIST);
		}

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> tresp =
				this.getDidContractService()
				.addPublicKey(
						identity,
						req.getPublicKey(),
						req.getType().getTypeName(),
						index);

		if (tresp.checkFail()) {
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
		SetDidAttrResp attrResp = new SetDidAttrResp();
		attrResp.setStatus(tresp.getData());
		attrResp.setTransactionInfo(tresp.getTransactionInfo());
		return BaseResp.buildSuccess(attrResp);
	}


	@Override
	public BaseResp<SetDidAttrResp> updatePublicKey(UpdatePublicKeyReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		req.setPublicKey(DidUtils.appendHexPrefix(req.getPublicKey()));

		if (!DidUtils.verifyUpdatePublicKeyArg(req)) {
			log.error("Failed to call `updatePublicKey()`: the `UpdatePublicKeyReq` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String didPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String did = DidUtils.generateDid(didPublicKey);

		if (!DidUtils.isValidDid(did)) {
			log.error(
					"Failed to call `updatePublicKey()`: the did or controller is invalid, did: {}",
					did
			);
			return BaseResp.build(RetEnum.RET_DID_INVALID);
		}

		String identity = DidUtils.convertDidToAddressStr(did);
		BaseResp<DocumentData> resp = this.getDidContractService().getDocument(identity);

		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}
		DocumentData doc =  resp.getData();
		if (StringUtils.equals(DidConst.DocumentStatus.DEACTIVATION.getTag(), doc.getStatus())) {
			log.error(
					"Failed to call `updatePublicKey()`: the identity has bean revocated, did: {}, status: {}",
					did, doc.getStatus()
			);
			return BaseResp.build(RetEnum.RET_DID_IDENTITY_ALREADY_REVOCATED);
		}
		Integer index = req.getIndex();
		boolean isExist = false;
		boolean isSame = false;
		String pubKeyStatus = commonConstant.EMPTY_STR;
		for (DocumentPubKeyData pubKey : doc.getPublicKey()) {
			String[] valueArray = StringUtils.splitByWholeSeparator(pubKey.getId(), commonConstant.SEPARATOR_DOCUMENT_PUBLICKEY_ID);
			if (index == Integer.parseInt(valueArray[1])) {
				pubKeyStatus = pubKey.getStatus();
				isExist = true;
				if(StringUtils.equals(pubKey.getPublicKeyHex(), req.getPublicKey())){
					isSame = true;
				}
				break;
			}
		}
		if (!isExist) {
			log.error(
					"Failed to call `updatePublicKey()`: the public key is not exist, index: {}",
					index
			);
			return BaseResp.build(RetEnum.RET_DID_PUBLICKEY_NOTEXIST);
		}
		if(isSame){
			log.error(
					"Failed to call `updatePublicKey()`: the public key is the same, index: {}, public key: {}",
					index, req.getPublicKey()
			);
			return BaseResp.build(RetEnum.RET_DID_UPDATE_PUBLICKEY_SAME);
		}

		if (StringUtils.equals(DidConst.DocumentAttrStatus.DID_PUBLICKEY_INVALID.getTag(), pubKeyStatus)) {
			log.error(
					"Failed to call `updatePublicKey()`: the public key has been revocated, status: {}",
					pubKeyStatus
			);
			return BaseResp.build(RetEnum.RET_DID_PUBLICKEY_ALREADY_REVOCATED);
		}

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> tresp =
				this.getDidContractService()
						.updatePublicKey(
								DidUtils.convertDidToAddressStr(did),
								req.getPublicKey(),
								req.getType().getTypeName(),
								index);

		if (tresp.checkFail()) {
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
		SetDidAttrResp attrResp = new SetDidAttrResp();
		attrResp.setStatus(tresp.getData());
		attrResp.setTransactionInfo(tresp.getTransactionInfo());
		return BaseResp.buildSuccess(attrResp);
	}

	@Override
	public BaseResp<SetDidAttrResp> revocationPublicKey(RevocationPublicKeyReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		req.setPublicKey(DidUtils.appendHexPrefix(req.getPublicKey()));

		if (!DidUtils.verifyRevocationPublicKeyArg(req)) {
			log.error("Failed to call `revocationPublicKey()`: the `RevocationPublickeyReq` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String didPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String did = DidUtils.generateDid(didPublicKey);
		String identity = DidUtils.convertDidToAddressStr(did);

		BaseResp<DocumentData> getDocResp = this.getDidContractService().getDocument(identity);
		if (getDocResp.checkFail()) {
			return BaseResp.build(getDocResp.getCode(), getDocResp.getErrMsg());
		}
		DocumentData docData = getDocResp.getData();
		if (StringUtils.equals(DidConst.DocumentStatus.DEACTIVATION.getTag(), docData.getStatus())) {
			log.error(
					"Failed to call `revocationPublicKey()`: the identity has bean revocated, did: {}, status: {}",
					did, docData.getStatus()
			);
			return BaseResp.build(RetEnum.RET_DID_IDENTITY_ALREADY_REVOCATED);
		}

		// The last valid publicKey cannot be revocated
		List<DocumentPubKeyData> publicKeys = DidUtils.getValidPublicKeys(docData);
		if (publicKeys.size() == 1 && StringUtils.equals(publicKeys.get(0).getPublicKeyHex(), req.getPublicKey())) {
			log.error("Failed to call `revocationPublicKey()`: Can not revocation the last public key");
			return BaseResp.build(RetEnum.RET_DID_CONNOT_REVOCATION_LAST_PUBLICKEY);
		}

		Integer index = 0;
		String type = commonConstant.EMPTY_STR;
		boolean isExist = false;
		for (DocumentPubKeyData pubKey : publicKeys) {
			if (StringUtils.equals(pubKey.getPublicKeyHex(), req.getPublicKey())) {
				String[] valueArray = StringUtils.splitByWholeSeparator(pubKey.getId(), commonConstant.SEPARATOR_DOCUMENT_PUBLICKEY_ID);
				index = Integer.valueOf(valueArray[1]);
				type = pubKey.getType();
				isExist = true;
				break;
			}
		}
		// Does not exist and has been revoked, both belong to non-existence
		if (!isExist) {
			log.error("Failed to call `revocationPublicKey()`: the public key index is not exist");
			return BaseResp.build(RetEnum.RET_DID_PUBLICKEY_NOTEXIST);
		}

		// revocation the publicKey
		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> tresp =
				this.getDidContractService()
						.revocationPublicKey(
								identity,
								req.getPublicKey(),
								type,
								index);

		if (tresp.checkFail()) {
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
		SetDidAttrResp attrResp = new SetDidAttrResp();
		attrResp.setStatus(tresp.getData());
		attrResp.setTransactionInfo(tresp.getTransactionInfo());

		return BaseResp.buildSuccess(attrResp);
	}

	@Override
	public BaseResp<SetDidAttrResp> setService(SetServiceReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		if (!DidUtils.verifySetServiceArg(req)) {
			log.error("Failed to call `setService()`: the `SetServiceReq` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String didPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String did = DidUtils.generateDid(didPublicKey);

		if (!DidUtils.isValidDid(did) ) {
			log.error(
					"Failed to call `setService()`: the addr convert base on `did` is illegal, did: {}",
					did
			);
			return BaseResp.build(RetEnum.RET_DID_INVALID);
		}
		// validate the service status
		if (DidConst.DocumentAttrStatus.DID_SERVICE_INVALID == req.getStatus()) {
			log.error(
					"Failed to call `setService()`: the service status is illegal, did: {}, status: {}",
					did, req.getStatus().getTag());
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}
		String identity = DidUtils.convertDidToAddressStr(did);
		BaseResp<DidConst.DocumentStatus> resp = this.getDidContractService().getStatus(identity);
		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}
		DidConst.DocumentStatus docStatus = resp.getData();
		if (DidConst.DocumentStatus.DEACTIVATION == docStatus) {
			log.error(
					"Failed to call `setService()`: the identity has bean revocated, did: {}, status: {}",
					did, docStatus
			);
			return BaseResp.build(RetEnum.RET_DID_IDENTITY_ALREADY_REVOCATED);
		}

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> tresp =
				this.getDidContractService()
						.setService(
								identity,
								req.getService().getId(),
								req.getService().getType(),
								req.getService().getServiceEndpoint(),
								req.getStatus());
		if (tresp.checkFail()) {
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
		SetDidAttrResp attrResp = new SetDidAttrResp();
		attrResp.setStatus(tresp.getData());
		attrResp.setTransactionInfo(tresp.getTransactionInfo());
		return BaseResp.buildSuccess(attrResp);
	}

	@Override
	public BaseResp<SetDidAttrResp> revocationService(SetServiceReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		if (!DidUtils.verifySetServiceArg(req)) {
			log.error("Failed to call `revocationService()`: the `SetServiceReq` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String didPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String did = DidUtils.generateDid(didPublicKey);

		if (!DidUtils.isValidDid(did) ) {
			log.error(
					"Failed to call `revocationService()`: the addr convert base on `did` is illegal, did: {}",
					did
			);
			return BaseResp.build(RetEnum.RET_DID_INVALID);
		}
		String identity = DidUtils.convertDidToAddressStr(did);
		BaseResp<DocumentData> resp = this.getDidContractService().getDocument(identity);
		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}

		DocumentData doc = resp.getData();
		if (StringUtils.equals(DidConst.DocumentStatus.DEACTIVATION.getTag(), doc.getStatus())) {
			log.error(
					"Failed to call `revocationService()`: the identity has bean revocated, did: {}, status: {}",
					did, doc.getStatus()
			);
			return BaseResp.build(RetEnum.RET_DID_IDENTITY_ALREADY_REVOCATED);
		}
		boolean isExist = false;
		String serviceStatus = commonConstant.EMPTY_STR;
		for (DocumentServiceData service : doc.getService()) {
			if (StringUtils.equals(service.getServiceEndpoint(),
					req.getService().getServiceEndpoint())) {
				serviceStatus = service.getStatus();
				isExist = true;
				break;
			}
		}

		if (!isExist) {
			log.error(
					"Failed to call `revocationService()`: the service is not exist"
			);
			return BaseResp.build(RetEnum.RET_DID_SET_SERVICE_NOTEXIST);
		}

		if (StringUtils.equals(DidConst.DocumentAttrStatus.DID_SERVICE_INVALID.getTag(), serviceStatus)) {
			log.error(
					"Failed to call `revocationService()`: the service has bean revocated, status: {}",
					serviceStatus
			);
			return BaseResp.build(RetEnum.RET_DID_SET_SERVICE_ALREADY_REVOCATED);
		}

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> tresp =
				this.getDidContractService()
						.setService(
								identity,
								req.getService().getId(),
								req.getService().getType(),
								req.getService().getServiceEndpoint(),
								DidConst.DocumentAttrStatus.DID_SERVICE_INVALID);

		if (tresp.checkFail()) {
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
		SetDidAttrResp attrResp = new SetDidAttrResp();
		attrResp.setStatus(tresp.getData());
		attrResp.setTransactionInfo(tresp.getTransactionInfo());
		return BaseResp.buildSuccess(attrResp);
	}



	public BaseResp<ChangeDocumentStatusResp> changeDocumentStatus(ChangeDocumentStatusReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}

		if (!DidUtils.verifyChangeDocumentStatusArg(req)) {
			log.error("Failed to call `changeDocumentStatus()`: the `ChangeDocumentStatusReq` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String didPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String did = DidUtils.generateDid(didPublicKey);

		if (!DidUtils.isValidDid(did) ) {
			log.error(
					"Failed to call `changeDocumentStatus()`: the addr convert base on `did` is illegal, did: {}",
					did
			);
			return BaseResp.build(RetEnum.RET_DID_INVALID);
		}

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> resp =
				this.getDidContractService()
				.changeStatus(DidUtils.convertDidToAddressStr(did), req.getStatus().getCode());
		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}
		ChangeDocumentStatusResp res = new ChangeDocumentStatusResp();
		res.setStatus(true);
		res.setTransactionInfo(resp.getTransactionInfo());
		return BaseResp.buildSuccess(res);
	}

	public BaseResp<DidConst.DocumentStatus> getDocumentStatus(String did) {
		if (!DidUtils.isValidDid(did) ) {
			log.error(
					"Failed to call `getDocumentStatus()`: the addr convert base on `did` is illegal, did: {}",
					did
			);
			return BaseResp.build(RetEnum.RET_DID_INVALID);
		}
		BaseResp<DidConst.DocumentStatus> resp = this.getDidContractService().getStatus(DidUtils.convertDidToAddressStr(did));
		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}
		return BaseResp.buildSuccess(resp.getData());
	}

	public BaseResp<Boolean> isDidExist(String did) {
		if (!DidUtils.isValidDid(did)) {
			log.error("Failed to call `isDidExist()`: the `did` is illegal");
			return BaseResp.build(RetEnum.RET_DID_INVALID);
		}
		return this.getDidContractService().isIdentityExist(DidUtils.convertDidToAddressStr(did));
	}


}
