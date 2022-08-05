package network.platon.did.sdk.service.impl;

import com.platon.crypto.ECKeyPair;
import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.did.common.config.DidConfig;
import network.platon.did.common.constant.ClaimMetaKey;
import network.platon.did.common.constant.VpOrVcPoofKey;
import network.platon.did.common.enums.AlgorithmTypeEnum;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.common.utils.DateUtils;
import network.platon.did.csies.algorithm.AlgorithmHandler;
import network.platon.did.csies.utils.ConverDataUtils;
import network.platon.did.csies.utils.Sha256;
import network.platon.did.sdk.base.dto.CheckData;
import network.platon.did.sdk.base.dto.Credential;
import network.platon.did.sdk.req.credential.CreateCredentialReq;
import network.platon.did.sdk.req.credential.CreateSelectCredentialReq;
import network.platon.did.sdk.req.credential.VerifyCredentialReq;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.credential.CreateCredentialResp;
import network.platon.did.sdk.resp.credential.CreateSelectCredentialResp;
import network.platon.did.sdk.service.BusinessBaseService;
import network.platon.did.sdk.service.CredentialService;
import network.platon.did.sdk.utils.CredentialsUtils;
import network.platon.did.sdk.utils.DidUtils;
import network.platon.did.sdk.utils.PresentationUtils;
import network.platon.did.sdk.utils.VerifyInputDataUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

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

		BaseResp<CheckData> checkResp = VerifyInputDataUtils.checkBaseData(req.getDid(), req.getIssuer(), req.getPublicKeyId(), req.getPrivateKey(), req.getPctId(), req.getClaim());

		if (checkResp.checkFail()) {
			return BaseResp.build(checkResp.getCode(),checkResp.getErrMsg());
		}

		// Build a credential entity
		Credential credential = this.generateCredential(req);

		// Get random salt
		HashMap<String, Object> claimMap =  (HashMap<String, Object>) credential.getClaimData();
		Map<String, Object> saltMap = ConverDataUtils.clone(claimMap);

		// get claim data seed and salt map
		Random r = new Random();
		long oneRandom = r.nextLong();
		if(oneRandom < 0) oneRandom = - oneRandom;
		byte[] seed = Sha256.uint64ToByte(new BigInteger(String.valueOf(oneRandom)));
		CredentialsUtils.generateSalt(saltMap, seed);

		// Get sign data
		String rawData = CredentialsUtils.getCredentialHash(credential, saltMap, null, seed);
		// Get signature
		String signature = AlgorithmHandler.signMessageStr(rawData, req.getPrivateKey());

		//Generate proof
		Map<String, Object> proof = new HashMap<>();
		proof.put(VpOrVcPoofKey.PROOF_VERIFICATIONMETHOD, req.getPublicKeyId());
		proof.put(VpOrVcPoofKey.PROOF_JWS, signature);
		proof.put(VpOrVcPoofKey.PROOF_CTEATED, credential.getIssuanceDate());
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

		@SuppressWarnings("unchecked")
		Map<String, Object> disclosureMap = (Map<String, Object>)credential.getProof().get(VpOrVcPoofKey.PROOF_DISCLOSURES);

		if(!CredentialsUtils.verifyClaimDataRootHash(credential.getClaimData(), disclosureMap)){
			return BaseResp.buildError(RetEnum.RET_CREDENTIAL_VERIFY_ERROR);
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

		@SuppressWarnings("unchecked")
		Map<String, Object> disclosureMap = (Map<String, Object>)credential.getProof().get(VpOrVcPoofKey.PROOF_DISCLOSURES);

		if(!CredentialsUtils.verifyClaimDataRootHash(credential.getClaimData(), disclosureMap)){
			return BaseResp.buildError(RetEnum.RET_CREDENTIAL_VERIFY_ERROR);
		}

		if (!CredentialsUtils.verifyEccSignature(credential.obtainHash(), credential.obtainSign(), checkResp.getData().getPublicKeyHex())) {
			return BaseResp.buildError(RetEnum.RET_CREDENTIAL_VERIFY_ERROR);
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
		credential.setHolder(req.getDid());
		credential.setIssuanceDate(issuanceDate);
		credential.setContext(req.getContext());
		credential.setClaimData(req.getClaim());
		credential.setExpirationDate(expireDate);
		credential.setId(ConverDataUtils.generalUUID());
		credential.setVersion(DidConfig.getVERSION());
		List<String> type = new ArrayList<>();
		type.add(String.valueOf(req.getType()));
		credential.setType(type);

		credential.setIssuer(req.getIssuer());
		Map<String, Object> claimMate = new HashMap<>();
		claimMate.put(ClaimMetaKey.PCTID, req.getPctId());
		credential.setClaimMeta(claimMate);
		return credential;
	}

}
