package network.platon.did.sdk.utils;

import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;

import network.platon.did.common.constant.ClaimMetaKey;
import network.platon.did.common.constant.VpOrVcPoofKey;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.csies.algorithm.AlgorithmHandler;
import network.platon.did.csies.utils.ConverDataUtils;
import network.platon.did.sdk.base.dto.*;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.contract.service.PctContractService;
import network.platon.did.sdk.contract.service.DidContractService;
import network.platon.did.sdk.contract.service.impl.PctContractServiceImpl;
import network.platon.did.sdk.contract.service.impl.DidContracServiceImpl;
import network.platon.did.sdk.resp.BaseResp;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class VerifyInputDataUtils {

	public static RetEnum checkDocumentData(DocumentData documentData, String publicKeyId,String privateKey,CheckData checkData) {
		if(null == documentData) {
			log.error("get did document data is null");
			return RetEnum.RET_DID_IDENTITY_NOTEXIST;
		}
		if(DidConst.DocumentStatus.DEACTIVATION.getTag().equals(documentData.getStatus())) {
			log.error("get did document data is null");
			return RetEnum.RET_DID_IDENTITY_ALREADY_REVOCATED;
		}
		String publicKeyHex = null;
		for(DocumentPubKeyData publicKey:documentData.getPublicKey()) {
			if(publicKey.getId().equals(publicKeyId)) {
				if(DidConst.DocumentAttrStatus.DID_PUBLICKEY_INVALID.getTag().equals(publicKey.getStatus())) {
					return RetEnum.RET_CREDENTIAL_PUBLICKEY_STATUS_INVAILD;
				}
				publicKeyHex = publicKey.getPublicKeyHex();
			}
		}

		if(StringUtils.isBlank(publicKeyHex)) {
			log.error(" did document  not found publicKeyId:{}",publicKeyId);
			return RetEnum.RET_CREDENTIAL_MATCH_PUBLICKEYID_ERROR;
		}
		if(StringUtils.isNotBlank(privateKey)) {
			BigInteger ppublicKey = AlgorithmHandler.publicKeyFromPrivate(Numeric.toBigInt(privateKey));
			if(ppublicKey.compareTo(Numeric.toBigInt(publicKeyHex)) != 0) {
				log.error("did document publickey not match privatekey");
				return RetEnum.RET_CREDENTIAL_MATCH_PUB_PRI_ERROR;
			}
		}
		checkData.setPublicKeyHex(publicKeyHex);
		return RetEnum.RET_SUCCESS;
	}
	

	public static RetEnum checkPct(String pctJson,Map<String, Object> claim) {
		HashMap<String, Object> newClaim = ConverDataUtils.clone((HashMap<String, Object>)claim);
		newClaim.remove(DidConst.CLAIMROOTHASH);
		newClaim.remove(DidConst.CLAIMSEED);

		//TODO 补充claim数据判断
		if(StringUtils.isBlank(pctJson)) {
			log.error("get pct json is null");
			return RetEnum.RET_CREDENTIAL_GET_PCT_ERROR;
		}
		String jsonData = ConverDataUtils.serialize(newClaim);
		if(!ConverDataUtils.isValidateJsonVersusSchema(jsonData, pctJson)) {
			log.error("json isValidateJsonVersusSchema fail, jsonData:{},pctJson:{}",jsonData,pctJson);
			return RetEnum.RET_CREDENTIAL_PCT_MATCH_ERROR;
		}
		return RetEnum.RET_SUCCESS;
	}
	
	
	public static RetEnum checkMap(Map<String, Object> claimMate,Map<String, Object> proof) {
		if(!proof.containsKey(VpOrVcPoofKey.PROOF_VERIFICATIONMETHOD) || !proof.containsKey(VpOrVcPoofKey.PROOF_JWS)
				|| !proof.containsKey(VpOrVcPoofKey.PROOF_CTEATED) || !proof.containsKey(VpOrVcPoofKey.PROOF_TYPE)) {
			return RetEnum.RET_COMMON_PARAM_PROOF_INVALID;
		}
		if(!claimMate.containsKey(ClaimMetaKey.PCTID)) {
			return RetEnum.RET_COMMON_PARAM_CLAIMMATE_INVALID;
		}
		return RetEnum.RET_SUCCESS;
	}
	

	public static BaseResp<CheckData> checkBaseData(String holder, String issuer, String publicKeyId, String privateKey, String pctId, Map<String, Object> claim){
		String holderAddress = DidUtils.convertDidToAddressStr(holder);
		if(StringUtils.isBlank(holderAddress)) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,"holder invaild",null);
		}
		DidContractService didContractService = new DidContracServiceImpl();
		BaseResp<Boolean> isExist = didContractService.isIdentityExist(holderAddress);
		if(isExist.checkFail() || !isExist.getData()) {
			return BaseResp.build(RetEnum.RET_CREDENTIAL_DID_NOT_FOUND);
		}
		
		//Determine whether the document data is consistent
		String didAddress = DidUtils.convertDidToAddressStr(issuer);
		if(StringUtils.isBlank(didAddress)) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID,"issuer invaild",null);
		}
		BaseResp<DocumentData> getDocResp = didContractService.getDocument(DidUtils.convertDidToAddressStr(issuer));
		if(!getDocResp.checkSuccess()) {
			log.error("get did document data fail.{}", getDocResp.getErrMsg());
			return BaseResp.build(getDocResp.getCode(),getDocResp.getErrMsg(),null);
		}
		DocumentData documentData = (DocumentData) getDocResp.getData();
		CheckData checkData = new CheckData();
		RetEnum retEnum = VerifyInputDataUtils.checkDocumentData(documentData, publicKeyId, privateKey,checkData);
		if(!RetEnum.isSuccess(retEnum)) {
			return BaseResp.buildError(retEnum);
		}
		
		PctContractService pctContractService = new PctContractServiceImpl();
		BaseResp<PctData> resp = pctContractService.queryPctById(pctId);
		if(!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(),resp.getErrMsg(),null);
		}
		retEnum = VerifyInputDataUtils.checkPct(resp.getData().getPctJson(), claim);
		if(!RetEnum.isSuccess(retEnum)) {
			return BaseResp.buildError(retEnum);
		}
		
		return BaseResp.buildSuccess(checkData);
	}
}
