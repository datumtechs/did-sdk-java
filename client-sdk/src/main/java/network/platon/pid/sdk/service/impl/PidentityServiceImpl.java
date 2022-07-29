package network.platon.pid.sdk.service.impl;

import com.platon.crypto.ECKeyPair;
import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.contract.dto.InitContractData;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import network.platon.pid.sdk.base.dto.*;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.commonConstant;
import network.platon.pid.sdk.contract.service.PidContractService;
import network.platon.pid.sdk.req.pid.*;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.TransactionResp;
import network.platon.pid.sdk.resp.pid.*;
import network.platon.pid.sdk.service.BusinessBaseService;
import network.platon.pid.sdk.service.PidentityService;
import network.platon.pid.sdk.utils.PidUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

@Slf4j
public class PidentityServiceImpl extends BusinessBaseService implements PidentityService,Serializable,Cloneable {

	private static final long serialVersionUID = 5732175037207593062L;
	
	private static PidentityServiceImpl pidentityServiceImpl = new PidentityServiceImpl();
	
	public static PidentityServiceImpl getInstance(){
        try {
            return (PidentityServiceImpl) pidentityServiceImpl.clone();
        } catch (CloneNotSupportedException e) {
        	log.error("get instance error.", e);
        }
        return new PidentityServiceImpl();
    }

	public BaseResp<CreatePidResp> createPid(CreatePidReq req) {

		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData(), null);
		}

		if (!PidUtils.isPrivateKeyValid(req.getPrivateKey())) {
			log.error("Failed to call `createPid()`: the `privateKey` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String pidPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String pid = PidUtils.generatePid(pidPublicKey);

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> tresp =
				this.getPidContractService()
						.createPid(pid, req.getPublicKey(), req.getType());
		if (tresp.checkFail()) {
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
		CreatePidResp createPidResp = new CreatePidResp();
		createPidResp.setPrivateKey(req.getPrivateKey());
		createPidResp.setPid(pid);
		createPidResp.setPublicKey(req.getPublicKey());
		createPidResp.setType(req.getType());
		createPidResp.setTransactionInfo(tresp.getTransactionInfo());
		return BaseResp.buildSuccess(createPidResp);
	}


	@Override
	public BaseResp<QueryPidDocumentResp> queryPidDocument(QueryPidDocumentReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		String pid = req.getPid();
		return this.queryPidDocument(pid);
	}

	public BaseResp<QueryPidDocumentDataResp> queryPidDocumentData (QueryPidDocumentReq req){
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		String pid = req.getPid();

		BaseResp<DocumentData> getDocResp = this.getPidContractService().getDocument(PidUtils.convertPidToAddressStr(pid));
		if (getDocResp.checkFail()) {
			return BaseResp.build(getDocResp.getCode(), getDocResp.getErrMsg());
		}
		return BaseResp.buildSuccess(new QueryPidDocumentDataResp(getDocResp.getData()));
	}

	private BaseResp<QueryPidDocumentResp> queryPidDocument(String pid) {

		if(!PidUtils.isValidPid(pid)) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}
		BaseResp<DocumentData> getDocResp = this.getPidContractService().getDocument(PidUtils.convertPidToAddressStr(pid));
		if (getDocResp.checkFail()) {
			return BaseResp.build(getDocResp.getCode(), getDocResp.getErrMsg());
		}
		// convert DocumentData to DocumentPojo
		Document doc = PidUtils.assembleDocumentPojo(getDocResp.getData());
		return BaseResp.buildSuccess(new QueryPidDocumentResp(doc));
	}

	@Override
	public BaseResp<SetPidAttrResp> addPublicKey(AddPublicKeyReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		req.setPublicKey(PidUtils.appendHexPrefix(req.getPublicKey()));

		if (!PidUtils.verifyAddPublicKeyArg(req)) {
			log.error("Failed to call `addPublicKey()`: the `AddPublickeyReq` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String pidPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String pid = PidUtils.generatePid(pidPublicKey);

		String identity = PidUtils.convertPidToAddressStr(pid);
		BaseResp<DocumentData> resp = this.getPidContractService().getDocument(identity);
		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}
		DocumentData doc = resp.getData();
		if (StringUtils.equals(PidConst.DocumentStatus.DEACTIVATION.getTag(), doc.getStatus())) {
			log.error(
					"Failed to call `addPublicKey()`: the identity has bean revocated, pid: {}, status: {}",
					pid, doc.getStatus()
			);
			return BaseResp.build(RetEnum.RET_PID_IDENTITY_ALREADY_REVOCATED);
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
			return BaseResp.build(RetEnum.RET_PID_PUBLICKEY_ALREADY_EXIST);
		}

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> tresp =
				this.getPidContractService()
				.addPublicKey(
						identity,
						req.getPublicKey(),
						req.getType().getTypeName(),
						index);

		if (tresp.checkFail()) {
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
		SetPidAttrResp attrResp = new SetPidAttrResp();
		attrResp.setStatus(tresp.getData());
		attrResp.setTransactionInfo(tresp.getTransactionInfo());
		return BaseResp.buildSuccess(attrResp);
	}


	@Override
	public BaseResp<SetPidAttrResp> updatePublicKey(UpdatePublicKeyReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		req.setPublicKey(PidUtils.appendHexPrefix(req.getPublicKey()));

		if (!PidUtils.verifyUpdatePublicKeyArg(req)) {
			log.error("Failed to call `updatePublicKey()`: the `UpdatePublicKeyReq` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String pidPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String pid = PidUtils.generatePid(pidPublicKey);

		if (!PidUtils.isValidPid(pid)) {
			log.error(
					"Failed to call `updatePublicKey()`: the pid or controller is invalid, pid: {}",
					pid
			);
			return BaseResp.build(RetEnum.RET_PID_INVALID);
		}

		String identity = PidUtils.convertPidToAddressStr(pid);
		BaseResp<DocumentData> resp = this.getPidContractService().getDocument(identity);

		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}
		DocumentData doc =  resp.getData();
		if (StringUtils.equals(PidConst.DocumentStatus.DEACTIVATION.getTag(), doc.getStatus())) {
			log.error(
					"Failed to call `updatePublicKey()`: the identity has bean revocated, pid: {}, status: {}",
					pid, doc.getStatus()
			);
			return BaseResp.build(RetEnum.RET_PID_IDENTITY_ALREADY_REVOCATED);
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
			return BaseResp.build(RetEnum.RET_PID_PUBLICKEY_NOTEXIST);
		}
		if(isSame){
			log.error(
					"Failed to call `updatePublicKey()`: the public key is the same, index: {}, public key: {}",
					index, req.getPublicKey()
			);
			return BaseResp.build(RetEnum.RET_PID_UPDATE_PUBLICKEY_SAME);
		}

		if (StringUtils.equals(PidConst.DocumentAttrStatus.PID_PUBLICKEY_INVALID.getTag(), pubKeyStatus)) {
			log.error(
					"Failed to call `updatePublicKey()`: the public key has been revocated, status: {}",
					pubKeyStatus
			);
			return BaseResp.build(RetEnum.RET_PID_PUBLICKEY_ALREADY_REVOCATED);
		}

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> tresp =
				this.getPidContractService()
						.updatePublicKey(
								PidUtils.convertPidToAddressStr(pid),
								req.getPublicKey(),
								req.getType().getTypeName(),
								index);

		if (tresp.checkFail()) {
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
		SetPidAttrResp attrResp = new SetPidAttrResp();
		attrResp.setStatus(tresp.getData());
		attrResp.setTransactionInfo(tresp.getTransactionInfo());
		return BaseResp.buildSuccess(attrResp);
	}

	@Override
	public BaseResp<SetPidAttrResp> revocationPublicKey(RevocationPublicKeyReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		req.setPublicKey(PidUtils.appendHexPrefix(req.getPublicKey()));

		if (!PidUtils.verifyRevocationPublicKeyArg(req)) {
			log.error("Failed to call `revocationPublicKey()`: the `RevocationPublickeyReq` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String pidPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String pid = PidUtils.generatePid(pidPublicKey);
		String identity = PidUtils.convertPidToAddressStr(pid);

		BaseResp<DocumentData> getDocResp = this.getPidContractService().getDocument(identity);
		if (getDocResp.checkFail()) {
			return BaseResp.build(getDocResp.getCode(), getDocResp.getErrMsg());
		}
		DocumentData docData = getDocResp.getData();
		if (StringUtils.equals(PidConst.DocumentStatus.DEACTIVATION.getTag(), docData.getStatus())) {
			log.error(
					"Failed to call `revocationPublicKey()`: the identity has bean revocated, pid: {}, status: {}",
					pid, docData.getStatus()
			);
			return BaseResp.build(RetEnum.RET_PID_IDENTITY_ALREADY_REVOCATED);
		}

		// The last valid publicKey cannot be revocated
		List<DocumentPubKeyData> publicKeys = PidUtils.getValidPublicKeys(docData);
		if (publicKeys.size() == 1 && StringUtils.equals(publicKeys.get(0).getPublicKeyHex(), req.getPublicKey())) {
			log.error("Failed to call `revocationPublicKey()`: Can not revocation the last public key");
			return BaseResp.build(RetEnum.RET_PID_CONNOT_REVOCATION_LAST_PUBLICKEY);
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
			return BaseResp.build(RetEnum.RET_PID_PUBLICKEY_NOTEXIST);
		}

		// revocation the publicKey
		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> tresp =
				this.getPidContractService()
						.revocationPublicKey(
								identity,
								req.getPublicKey(),
								type,
								index);

		if (tresp.checkFail()) {
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
		SetPidAttrResp attrResp = new SetPidAttrResp();
		attrResp.setStatus(tresp.getData());
		attrResp.setTransactionInfo(tresp.getTransactionInfo());

		return BaseResp.buildSuccess(attrResp);
	}

	@Override
	public BaseResp<SetPidAttrResp> setService(SetServiceReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		if (!PidUtils.verifySetServiceArg(req)) {
			log.error("Failed to call `setService()`: the `SetServiceReq` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String pidPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String pid = PidUtils.generatePid(pidPublicKey);

		if (!PidUtils.isValidPid(pid) ) {
			log.error(
					"Failed to call `setService()`: the addr convert base on `pid` is illegal, pid: {}",
					pid
			);
			return BaseResp.build(RetEnum.RET_PID_INVALID);
		}
		// validate the service status
		if (PidConst.DocumentAttrStatus.PID_SERVICE_INVALID == req.getStatus()) {
			log.error(
					"Failed to call `setService()`: the service status is illegal, pid: {}, status: {}",
					pid, req.getStatus().getTag());
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}
		String identity = PidUtils.convertPidToAddressStr(pid);
		BaseResp<PidConst.DocumentStatus> resp = this.getPidContractService().getStatus(identity);
		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}
		PidConst.DocumentStatus docStatus = resp.getData();
		if (PidConst.DocumentStatus.DEACTIVATION == docStatus) {
			log.error(
					"Failed to call `setService()`: the identity has bean revocated, pid: {}, status: {}",
					pid, docStatus
			);
			return BaseResp.build(RetEnum.RET_PID_IDENTITY_ALREADY_REVOCATED);
		}

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> tresp =
				this.getPidContractService()
						.setService(
								identity,
								req.getService().getId(),
								req.getService().getType(),
								req.getService().getServiceEndpoint(),
								req.getStatus());
		if (tresp.checkFail()) {
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
		SetPidAttrResp attrResp = new SetPidAttrResp();
		attrResp.setStatus(tresp.getData());
		attrResp.setTransactionInfo(tresp.getTransactionInfo());
		return BaseResp.buildSuccess(attrResp);
	}

	@Override
	public BaseResp<SetPidAttrResp> revocationService(SetServiceReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		if (!PidUtils.verifySetServiceArg(req)) {
			log.error("Failed to call `revocationService()`: the `SetServiceReq` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String pidPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String pid = PidUtils.generatePid(pidPublicKey);

		if (!PidUtils.isValidPid(pid) ) {
			log.error(
					"Failed to call `revocationService()`: the addr convert base on `pid` is illegal, pid: {}",
					pid
			);
			return BaseResp.build(RetEnum.RET_PID_INVALID);
		}
		String identity = PidUtils.convertPidToAddressStr(pid);
		BaseResp<DocumentData> resp = this.getPidContractService().getDocument(identity);
		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}

		DocumentData doc = resp.getData();
		if (StringUtils.equals(PidConst.DocumentStatus.DEACTIVATION.getTag(), doc.getStatus())) {
			log.error(
					"Failed to call `revocationService()`: the identity has bean revocated, pid: {}, status: {}",
					pid, doc.getStatus()
			);
			return BaseResp.build(RetEnum.RET_PID_IDENTITY_ALREADY_REVOCATED);
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
			return BaseResp.build(RetEnum.RET_PID_SET_SERVICE_NOTEXIST);
		}

		if (StringUtils.equals(PidConst.DocumentAttrStatus.PID_SERVICE_INVALID.getTag(), serviceStatus)) {
			log.error(
					"Failed to call `revocationService()`: the service has bean revocated, status: {}",
					serviceStatus
			);
			return BaseResp.build(RetEnum.RET_PID_SET_SERVICE_ALREADY_REVOCATED);
		}

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> tresp =
				this.getPidContractService()
						.setService(
								identity,
								req.getService().getId(),
								req.getService().getType(),
								req.getService().getServiceEndpoint(),
								PidConst.DocumentAttrStatus.PID_SERVICE_INVALID);

		if (tresp.checkFail()) {
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
		SetPidAttrResp attrResp = new SetPidAttrResp();
		attrResp.setStatus(tresp.getData());
		attrResp.setTransactionInfo(tresp.getTransactionInfo());
		return BaseResp.buildSuccess(attrResp);
	}



	public BaseResp<ChangeDocumentStatusResp> changeDocumentStatus(ChangeDocumentStatusReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}

		if (!PidUtils.verifyChangeDocumentStatusArg(req)) {
			log.error("Failed to call `changeDocumentStatus()`: the `ChangeDocumentStatusReq` is illegal");
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID);
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
		String pidPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String pid = PidUtils.generatePid(pidPublicKey);

		if (!PidUtils.isValidPid(pid) ) {
			log.error(
					"Failed to call `changeDocumentStatus()`: the addr convert base on `pid` is illegal, pid: {}",
					pid
			);
			return BaseResp.build(RetEnum.RET_PID_INVALID);
		}

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> resp =
				this.getPidContractService()
				.changeStatus(PidUtils.convertPidToAddressStr(pid), req.getStatus().getCode());
		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}
		ChangeDocumentStatusResp res = new ChangeDocumentStatusResp();
		res.setStatus(true);
		res.setTransactionInfo(resp.getTransactionInfo());
		return BaseResp.buildSuccess(res);
	}

	public BaseResp<PidConst.DocumentStatus> getDocumentStatus(String pid) {
		if (!PidUtils.isValidPid(pid) ) {
			log.error(
					"Failed to call `getDocumentStatus()`: the addr convert base on `pid` is illegal, pid: {}",
					pid
			);
			return BaseResp.build(RetEnum.RET_PID_INVALID);
		}
		BaseResp<PidConst.DocumentStatus> resp = this.getPidContractService().getStatus(PidUtils.convertPidToAddressStr(pid));
		if (resp.checkFail()) {
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}
		return BaseResp.buildSuccess(resp.getData());
	}

	public BaseResp<Boolean> isPidExist(String pid) {
		if (!PidUtils.isValidPid(pid)) {
			log.error("Failed to call `isPidExist()`: the `pid` is illegal");
			return BaseResp.build(RetEnum.RET_PID_INVALID);
		}
		return this.getPidContractService().isIdentityExist(PidUtils.convertPidToAddressStr(pid));
	}


}
