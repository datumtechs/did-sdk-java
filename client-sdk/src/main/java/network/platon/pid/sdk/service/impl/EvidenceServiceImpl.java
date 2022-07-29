package network.platon.pid.sdk.service.impl;

import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.common.utils.DateUtils;
import network.platon.pid.contract.dto.CredentialEvidence;
import network.platon.pid.contract.dto.InitContractData;
import network.platon.pid.sdk.base.dto.CheckData;
import network.platon.pid.sdk.base.dto.Credential;
import network.platon.pid.sdk.base.dto.EvidenceSignInfo;
import network.platon.pid.sdk.enums.CredentialStatus;
import network.platon.pid.sdk.req.evidence.*;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.TransactionResp;
import network.platon.pid.sdk.resp.evidence.CreateEvidenceResp;
import network.platon.pid.sdk.resp.evidence.QueryEvidenceResp;
import network.platon.pid.sdk.resp.evidence.RevokeEvidenceResp;
import network.platon.pid.sdk.service.BusinessBaseService;
import network.platon.pid.sdk.service.EvidenceService;
import network.platon.pid.sdk.utils.CredentialsUtils;
import network.platon.pid.sdk.utils.VerifyInputDataUtils;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The specific implementation of evidence vouchers, including storing vouchers in PlatON, querying data, etc
 * @Auther: Rongjin Zhang
 * @Date: 2020年6月16日
 * @Description:
 */
@Slf4j
public class EvidenceServiceImpl extends BusinessBaseService implements EvidenceService,Serializable,Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5732175037207593062L;
	
	private static EvidenceServiceImpl evidenceServiceImpl = new EvidenceServiceImpl();
	
	public static EvidenceServiceImpl getInstance(){
        try {
            return (EvidenceServiceImpl) evidenceServiceImpl.clone();
        } catch (CloneNotSupportedException e) {
        	log.error("get instance error.", e);
        }
        return new EvidenceServiceImpl();
    }
	
	@Override
	public BaseResp<CreateEvidenceResp> createEvidence(CreateEvidenceReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.getCode() != RetEnum.RET_SUCCESS.getCode()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		Credential credential = req.getCredential();
		BaseResp<CheckData> checkResp = VerifyInputDataUtils.checkBaseData(credential.getHolder(), 
				credential.getIssuer(), credential.obtainPublickeyId(), req.getPrivateKey(), credential.obtainPctId(), credential.getClaimData());
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
		
		//Stored here is the credential data hash including proof
		String credentialHash = credential.obtainHash();
		TransactionResp<Boolean> isExist = getCredentialContractService().isHashExit(credentialHash);
		if(isExist.checkFail() || isExist.getData()) {
			return BaseResp.build(RetEnum.RET_EVIDENCE_EXIST_ERROR);
		}

		String createTime = DateUtils.convertTimestampToUtc(DateUtils.getCurrentTimeStamp());
		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<String> resp = getCredentialContractService()
				.createCredentialEvience(credentialHash, checkResp.getData().getPublicKeyHex(), credential.obtainSign(), createTime);
		if(!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(),resp.getErrMsg());
		}
		CreateEvidenceResp createEvidenceResp = new CreateEvidenceResp();
		createEvidenceResp.setEvidenceId(credentialHash);
		createEvidenceResp.setTransactionInfo(resp.getTransactionInfo());
		return BaseResp.buildSuccess(createEvidenceResp);
	}

	@Override
	public BaseResp<QueryEvidenceResp> queryEvidence(QueryEvidenceReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.getCode() != RetEnum.RET_SUCCESS.getCode()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		TransactionResp<CredentialEvidence> resp = getCredentialContractService().queryCredentialEvience(req.getEvidenceId());
		if(!resp.checkSuccess()) {
			return BaseResp.build(resp.getCode(),resp.getErrMsg());
		}
		TransactionResp<BigInteger> status = this.getCredentialContractService().getStatus(req.getEvidenceId());
		if(status.checkFail()){
			return BaseResp.build(status.getCode(),status.getErrMsg());
		}
		QueryEvidenceResp queryEvidenceResp = new QueryEvidenceResp();
		queryEvidenceResp.setCredentialHash(req.getEvidenceId());
		EvidenceSignInfo evidenceSignInfo = new EvidenceSignInfo();
		evidenceSignInfo.setSignature(resp.getData().getSignaturedata());
		evidenceSignInfo.setTimestamp(resp.getData().getCreate());
		evidenceSignInfo.setSigner(resp.getData().getSigner());
		queryEvidenceResp.setStatus(CredentialStatus.getStatusData(status.getData()).getDesc());
		queryEvidenceResp.setSignInfo(evidenceSignInfo);
		return BaseResp.buildSuccess(queryEvidenceResp);
	}

	@Override
	public BaseResp<RevokeEvidenceResp> revokeEvidence(RevokeEvidenceReq req) {
		String hash = req.getEvidenceId();
		TransactionResp<CredentialEvidence> resp = getCredentialContractService().queryCredentialEvience(hash);
		if(resp.checkFail()) {
			return BaseResp.build(resp.getCode(),resp.getErrMsg());
		}
		if(resp.getData() == null){
			return BaseResp.build(RetEnum.RET_EVIDENCE_NOT_EXIST_ERROR);
		}

		TransactionResp<BigInteger> status = this.getCredentialContractService().getStatus(hash);
		if(status.checkFail()){
			return BaseResp.build(status.getCode(),status.getErrMsg());
		}
		if(CredentialStatus.checkFail(status.getData())){
			return BaseResp.build(RetEnum.RET_EVIDENCE_STATUS_INVALID);
		}

		this.ChangePrivateKey(req.getPrivateKey());
		TransactionResp<Boolean> changeStatusResp = this.getCredentialContractService().changeStatus(hash, CredentialStatus.INVALID.getStatus());
		if(changeStatusResp.checkFail()){
			return BaseResp.build(changeStatusResp.getCode(),changeStatusResp.getErrMsg());
		}
		RevokeEvidenceResp res = new RevokeEvidenceResp();
		res.setStatus(true);
		res.setTransactionInfo(changeStatusResp.getTransactionInfo());
		return BaseResp.buildSuccess(res);
	}

	@Override
	public BaseResp<String> verifyCredentialEvidence(VerifyCredentialEvidenceReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.getCode() != RetEnum.RET_SUCCESS.getCode()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}

		Credential credential = req.getCredential();
		BaseResp<CheckData> checkResp = VerifyInputDataUtils.checkBaseData(credential.getHolder(),
				credential.getIssuer(), credential.obtainPublickeyId(), null, credential.obtainPctId(), credential.getClaimData());
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

		//Query the data on the chain according to the hash calculated by credential
		String credentialHash = req.getCredential().obtainHash();

		TransactionResp<BigInteger> status = this.getCredentialContractService().getStatus(credentialHash);
		if(status.checkFail()){
			return BaseResp.build(status.getCode(),status.getErrMsg());
		}
		if(CredentialStatus.checkFail(status.getData())){
			return BaseResp.build(RetEnum.RET_EVIDENCE_STATUS_INVALID);
		}

		QueryEvidenceReq queryEvidenceReq = QueryEvidenceReq.builder().evidenceId(credentialHash).build();
		BaseResp<QueryEvidenceResp> resp = this.queryEvidence(queryEvidenceReq);
		if (resp.checkFail()) {
			log.error("Find evidence data fail.");
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}

		if( !credential.obtainSign().equals(resp.getData().getSignInfo().getSignature()) ||
				!checkResp.getData().getPublicKeyHex().equals((resp.getData().getSignInfo().getSigner()))){
			return BaseResp.buildError(RetEnum.RET_CREDENTIAL_VERIFY_ERROR);
		}

		if (!CredentialsUtils.verifyEccSignature(credential.obtainHash(), credential.obtainSign(), checkResp.getData().getPublicKeyHex())) {
			return BaseResp.buildError(RetEnum.RET_CREDENTIAL_VERIFY_ERROR);
		}

		return BaseResp.buildSuccess();
	}

}
