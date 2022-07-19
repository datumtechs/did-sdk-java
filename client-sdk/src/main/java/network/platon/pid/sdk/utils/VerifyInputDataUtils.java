package network.platon.pid.sdk.utils;

import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.constant.ClaimMetaKey;
import network.platon.pid.common.constant.VpOrVcPoofKey;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import network.platon.pid.csies.utils.ConverDataUtils;
import network.platon.pid.sdk.base.dto.CheckData;
import network.platon.pid.sdk.base.dto.DocumentAuthData;
import network.platon.pid.sdk.base.dto.DocumentData;
import network.platon.pid.sdk.base.dto.DocumentPubKeyData;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.contract.service.PctContractService;
import network.platon.pid.sdk.contract.service.PidContractService;
import network.platon.pid.sdk.contract.service.impl.PctContractServiceImpl;
import network.platon.pid.sdk.contract.service.impl.PidContracServiceImpl;
import network.platon.pid.sdk.resp.BaseResp;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.Map;

@Slf4j
public class VerifyInputDataUtils {

	public static RetEnum checkDocumentData(DocumentData documentData, String publicKeyId,String privateKey,CheckData checkData) {
		if(null == documentData) {
			log.error("get pid document data is null");
			return RetEnum.RET_PID_IDENTITY_NOTEXIST;
		}
		if(PidConst.DocumentStatus.DEACTIVATION.getTag().equals(documentData.getStatus())) {
			log.error("get pid document data is null");
			return RetEnum.RET_PID_IDENTITY_ALREADY_REVOCATED;
		}
		String publicKeyHex = null;
		for(DocumentPubKeyData publicKey:documentData.getPublicKey()) {
			if(publicKey.getId().equals(publicKeyId)) {
				if(PidConst.DocumentAttrStatus.PID_AUTH_INVALID.getTag().equals(publicKey.getStatus())) {
					return RetEnum.RET_CREDENTIAL_PUBLICKEY_STATUS_INVAILD;
				}
				publicKeyHex = publicKey.getPublicKeyHex();
			}
		}
		Boolean isAuth = false; 
		for(DocumentAuthData authData:documentData.getAuthentication()) {
			if(authData.getPublicKeyHex().equals(publicKeyHex)) {
				if(PidConst.DocumentAttrStatus.PID_AUTH_INVALID.getTag().equals(authData.getStatus())) {
					return RetEnum.RET_CREDENTIAL_AUTH_STATUS_INVAILD;
				}
				isAuth = true;
			}
		}
		if(!isAuth) {
			return RetEnum.RET_CREDENTIAL_PUBLICKEY_NOT_AUTH;
		}
		if(StringUtils.isBlank(publicKeyHex)) {
			log.error(" pid document  not found publicKeyId:{}",publicKeyId);
			return RetEnum.RET_CREDENTIAL_MATCH_PUBLICKEYID_ERROR;
		}
		if(StringUtils.isNotBlank(privateKey)) {
			BigInteger ppublicKey = AlgorithmHandler.publicKeyFromPrivate(Numeric.toBigInt(privateKey));
			if(ppublicKey.compareTo(Numeric.toBigInt(publicKeyHex)) != 0) {
				log.error("pid document publickey not match privatekey");
				return RetEnum.RET_CREDENTIAL_MATCH_PUB_PRI_ERROR;
			}
		}
		checkData.setPublicKeyHex(publicKeyHex);
		return RetEnum.RET_SUCCESS;
	}
	
	public static RetEnum checkPct(String pctJson,Map<String, Object> claim) {
		//TODO 补充claim数据判断
		if(StringUtils.isBlank(pctJson)) {
			log.error("get pct json is null");
			return RetEnum.RET_CREDENTIAL_GET_PCT_ERROR;
		}
		String jsonData = ConverDataUtils.serialize(claim);
		if(!ConverDataUtils.isValidateJsonVersusSchema(jsonData, pctJson)) {
			log.error("json isValidateJsonVersusSchema fail, jsonData:{},pctJson:{}",jsonData,pctJson);
			return RetEnum.RET_CREDENTIAL_PCT_MATCH_ERROR;
		}
		return RetEnum.RET_SUCCESS;
	}
	
	
	public static RetEnum checkMap(Map<String, Object> claimMate,Map<String, Object> proof) {
		if(!proof.containsKey(VpOrVcPoofKey.PROOF_VERIFICATIONMETHOD) || !proof.containsKey(VpOrVcPoofKey.PROOF_JWS)
				|| !proof.containsKey(VpOrVcPoofKey.PROOF_CTEATED) || !proof.containsKey(VpOrVcPoofKey.PROOF_TYPE)
				|| !proof.containsKey(VpOrVcPoofKey.PROOF_SALT)) {
			return RetEnum.RET_COMMON_PARAM_PROOF_INVALID;
		}
		if(!claimMate.containsKey(ClaimMetaKey.PCTID)) {
			return RetEnum.RET_COMMON_PARAM_CLAIMMATE_INVALID;
		}
		return RetEnum.RET_SUCCESS;
	}
	
	public static BaseResp<CheckData> checkBaseData(String holder, String issuer, String publicKeyId,String privateKey,String pctId, Map<String, Object> claim){
		String holderAddress = PidUtils.convertPidToAddressStr(holder);
		if(StringUtils.isBlank(holderAddress)) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,"holder invaild",null);
		}
		PidContractService pidContractService = new PidContracServiceImpl();
		BaseResp<Boolean> isExist = pidContractService.isIdentityExist(holderAddress);
		if(isExist.checkFail() || !isExist.getData()) {
			return BaseResp.build(RetEnum.RET_CREDENTIAL_PID_NOT_FOUND);
		}
		
		//Determine whether the document data is consistent
		String pidAddress = PidUtils.convertPidToAddressStr(issuer);
		if(StringUtils.isBlank(pidAddress)) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,"issuer invaild",null);
		}
		BaseResp<DocumentData> getDocResp = pidContractService.getDocument(PidUtils.convertPidToAddressStr(issuer));
		if(!getDocResp.checkSuccess()) {
			log.error("get pid document data fail.{}", getDocResp.getErrMsg());
			return BaseResp.build(getDocResp.getCode(),getDocResp.getErrMsg(),null);
		}
		DocumentData documentData = (DocumentData) getDocResp.getData();
		CheckData checkData = new CheckData();
		RetEnum retEnum = VerifyInputDataUtils.checkDocumentData(documentData, publicKeyId, privateKey,checkData);
		if(!RetEnum.isSuccess(retEnum)) {
			return BaseResp.buildError(retEnum);
		}
		
		PctContractService pctContractService = new PctContractServiceImpl();
		BaseResp<String> resp = pctContractService.queryPctById(pctId);
		if(!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(),resp.getErrMsg(),null);
		}
		retEnum = VerifyInputDataUtils.checkPct(resp.getData(), claim);
		if(!RetEnum.isSuccess(retEnum)) {
			return BaseResp.buildError(retEnum);
		}
		
		return BaseResp.buildSuccess(checkData);
	}
}
