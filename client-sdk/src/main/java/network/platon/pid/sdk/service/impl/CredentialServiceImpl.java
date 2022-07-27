package network.platon.pid.sdk.service.impl;

import com.platon.crypto.ECKeyPair;
import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.config.PidConfig;
import network.platon.pid.common.constant.ClaimMetaKey;
import network.platon.pid.common.constant.VpOrVcPoofKey;
import network.platon.pid.common.enums.AlgorithmTypeEnum;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.common.utils.DateUtils;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import network.platon.pid.csies.utils.ConverDataUtils;
import network.platon.pid.sdk.base.dto.CheckData;
import network.platon.pid.sdk.base.dto.Credential;
import network.platon.pid.sdk.req.credential.CreateCredentialReq;
import network.platon.pid.sdk.req.credential.CreateSelectCredentialReq;
import network.platon.pid.sdk.req.credential.VerifyCredentialReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.credential.CreateCredentialResp;
import network.platon.pid.sdk.resp.credential.CreateSelectCredentialResp;
import network.platon.pid.sdk.service.BusinessBaseService;
import network.platon.pid.sdk.service.CredentialService;
import network.platon.pid.sdk.utils.CredentialsUtils;
import network.platon.pid.sdk.utils.PidUtils;
import network.platon.pid.sdk.utils.PresentationUtils;
import network.platon.pid.sdk.utils.VerifyInputDataUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CredentialServiceImpl extends BusinessBaseService implements CredentialService,Serializable,Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5732175037207593062L;
	
	private static CredentialServiceImpl credentialServiceImpl = new CredentialServiceImpl();
	
	public static CredentialServiceImpl getInstance(){
        try {
            return (CredentialServiceImpl) credentialServiceImpl.clone();
        } catch (CloneNotSupportedException e) {
        	log.error("get instance error.", e);
        }
        return new CredentialServiceImpl();
    }
	
	@Override
	public BaseResp<CreateCredentialResp> createCredential(CreateCredentialReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getIssuerPrivateKey());
		String issuerPidPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String issuerPid = PidUtils.generatePid(issuerPidPublicKey);

		BaseResp<CheckData> checkResp = VerifyInputDataUtils.checkBaseData(req.getPid(), issuerPid, req.getPublicKeyId(), req.getPrivateKey(), req.getPctId(), req.getClaim());
		if (checkResp.checkFail()) {
			return BaseResp.build(checkResp.getCode(),checkResp.getErrMsg());
		}

		// Build a credential entity
		Credential credential = this.generateCredential(req);
		// Get random salt
		HashMap<String, Object> claimMap =  (HashMap<String, Object>) credential.getClaimData();
		Map<String, Object> saltMap = ConverDataUtils.clone(claimMap);
		CredentialsUtils.generateSalt(saltMap, null);

		// Get sign data
		String rawData = CredentialsUtils.getCredentialHash(credential, saltMap, null);
		// Get signature
		String signature = AlgorithmHandler.signMessageStr(rawData, req.getPrivateKey());

		//Generate proof
		Map<String, Object> proof = new HashMap<>();
		proof.put(VpOrVcPoofKey.PROOF_VERIFICATIONMETHOD, req.getPublicKeyId());
		proof.put(VpOrVcPoofKey.PROOF_JWS, signature);
		proof.put(VpOrVcPoofKey.PROOF_CTEATED, credential.getIssuanceDate());
		proof.put(VpOrVcPoofKey.PROOF_SALT, saltMap);
		proof.put(VpOrVcPoofKey.PROOF_TYPE, AlgorithmTypeEnum.ECC.getDesc());
		credential.setProof(proof);
		CreateCredentialResp createCredentialResp = new CreateCredentialResp();
		createCredentialResp.setCredential(credential);
		return BaseResp.buildSuccess(createCredentialResp);
	}

	@Override
	public BaseResp<String> verifyCredential(VerifyCredentialReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.getCode() != RetEnum.RET_SUCCESS.getCode()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		Credential credential = req.getCredential();
		//Determine whether the document data is consistent
		BaseResp<CheckData> checkResp = VerifyInputDataUtils.checkBaseData(credential.getHolder(), credential.getIssuer(), credential.obtainPublickeyId(), null, credential.obtainPctId(), credential.getClaimData());
		if (!checkResp.checkSuccess()) {
			return BaseResp.build(checkResp.getCode(),checkResp.getErrMsg());
		}
		Long expireData = DateUtils.convertUtcDateToNoMillisecondTime(credential.getExpirationDate());
		if(expireData < DateUtils.getNoMillisecondTimeStamp()){
			return BaseResp.build(RetEnum.RET_CREDENTIAL_EXPIRED);
		}

		RetEnum retEnum = VerifyInputDataUtils.checkMap(credential.getClaimMeta(), credential.getProof());
		if (!RetEnum.isSuccess(retEnum)) {
			return BaseResp.buildError(retEnum);
		}
		
		if (!CredentialsUtils.verifyEccSignature(credential.obtainHash(), credential.obtainSign(), checkResp.getData().getPublicKeyHex())) {
			return BaseResp.buildError(RetEnum.RET_CREDENTIAL_VERIFY_ERROR);
		}
		return BaseResp.buildSuccess();
	}

	@Override
	public BaseResp<CreateSelectCredentialResp> createSelectCredential(CreateSelectCredentialReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.checkFail()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		/**
		 * Build a credential entity
		 */
		Credential credential = req.getCredential();
		//Determine whether the document data is consistent
		BaseResp<CheckData> checkResp = VerifyInputDataUtils.checkBaseData(credential.getHolder(), credential.getIssuer(), credential.obtainPublickeyId(), null, credential.obtainPctId(), credential.getClaimData());
		if (!checkResp.checkSuccess()) {
			return BaseResp.build(checkResp.getCode(),checkResp.getErrMsg());
		}
		RetEnum retEnum = VerifyInputDataUtils.checkMap(credential.getClaimMeta(), credential.getProof());
		if (!RetEnum.isSuccess(retEnum)) {
			return BaseResp.buildError(retEnum);
		}
		PresentationUtils.addSelectSalt(req.getSelectMap(), credential.obtainSalt(), credential.getClaimData());
		credential.getProof().put(VpOrVcPoofKey.PROOF_DISCLOSURES, req.getSelectMap());
		CreateSelectCredentialResp createSelectCredentialResp = new CreateSelectCredentialResp();
		createSelectCredentialResp.setCredential(credential);
		return BaseResp.buildSuccess(createSelectCredentialResp);
	}


	private Credential generateCredential(CreateCredentialReq req) {
		String expireDate = DateUtils.convertTimestampToUtc(req.getExpirationDate());
		// issuanceDate is currentTime
		String issuanceDate = DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp());
		Credential credential = new Credential();
		credential.setHolder(req.getPid());
		credential.setIssuanceDate(issuanceDate);
		credential.setContext(req.getContext());
		credential.setClaimData(req.getClaim());
		credential.setExpirationDate(expireDate);
		credential.setId(ConverDataUtils.generalUUID());
		credential.setVersion(PidConfig.getVERSION());
		List<String> type = new ArrayList<>();
		type.add(String.valueOf(req.getType()));
		credential.setType(type);

		ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getIssuerPrivateKey());
		String issuerPidPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
		String issuerPid = PidUtils.generatePid(issuerPidPublicKey);

		credential.setIssuer(issuerPid);
		Map<String, Object> claimMate = new HashMap<>();
		claimMate.put(ClaimMetaKey.PCTID, req.getPctId());
		credential.setClaimMeta(claimMate);
		return credential;
	}

}
