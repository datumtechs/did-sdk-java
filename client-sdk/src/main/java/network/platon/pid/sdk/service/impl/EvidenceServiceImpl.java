package network.platon.pid.sdk.service.impl;

import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.common.utils.DateUtils;
import network.platon.pid.contract.dto.CredentialEvidence;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import network.platon.pid.sdk.base.dto.CheckData;
import network.platon.pid.sdk.base.dto.Credential;
import network.platon.pid.sdk.base.dto.DocumentData;
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
import network.platon.pid.sdk.utils.PidUtils;
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
		
		//Stored here is the credential data hash including proof
		String credentialHash = credential.obtainAllHash();
		TransactionResp<Boolean> isExist = getCredentialContractService().isHashExit(credentialHash);
		if(isExist.checkFail() || isExist.getData()) {
			return BaseResp.build(RetEnum.RET_EVIDENCE_EXIST_ERROR);
		}
		
		//Compute and sign hash data
		String evidenceSign = AlgorithmHandler.signMessageStr(credentialHash, req.getPrivateKey());
		String created = DateUtils.getCurrentTimeStampString();
		TransactionResp<String> resp = getCredentialContractService().createCredentialEvience(credentialHash, credential.getIssuer(), evidenceSign, created);
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
	public BaseResp<String> verifyEvidence(VerifyEvidenceReq req) {
		BaseResp<String> verifyBaseResp = req.validFiled();
		if (verifyBaseResp.getCode() != RetEnum.RET_SUCCESS.getCode()) {
			return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
		}
		TransactionResp<BigInteger> status = this.getCredentialContractService().getStatus(req.getCredentialHash());
		if(status.checkFail()){
			return BaseResp.build(status.getCode(),status.getErrMsg());
		}
		if(CredentialStatus.checkFail(status.getData())){
			return BaseResp.build(RetEnum.RET_EVIDENCE_STATUS_INVALID);
		}
		String signatureData = req.getEvidenceSignInfo().getSignature();
		// Determine whether the document data is consistent
		BaseResp<DocumentData> getDocResp = this.getPidContractService()
				.getDocument(PidUtils.convertPidToAddressStr(req.getEvidenceSignInfo().getSigner()));
		if (!getDocResp.checkSuccess()) {
			log.error("Get pid document data fail.{}", getDocResp.getErrMsg());
			return BaseResp.build(getDocResp.getCode(), getDocResp.getErrMsg());
		}
		DocumentData documentData = (DocumentData) getDocResp.getData();
		CheckData checkData = new CheckData();
		RetEnum retEnum = VerifyInputDataUtils.checkDocumentData(documentData, req.getPublicKeyId(), null, checkData);
		if (!RetEnum.isSuccess(retEnum)) {
			return BaseResp.buildError(retEnum);
		}
		Boolean isSuccess = AlgorithmHandler.verifySignature(req.getCredentialHash(), signatureData,
				Numeric.toBigInt(checkData.getPublicKeyHex()));
		if (!isSuccess) {
			log.error("Verify credential data fail. data:{},signatureData:{},publickey:{}", req.getCredentialHash(),
					signatureData, checkData.getPublicKeyHex());
			return BaseResp.build(RetEnum.RET_EVIDENCE_VERIFY_ERROR);
		}
		return BaseResp.buildSuccess();
	}

	@Override
	public BaseResp<RevokeEvidenceResp> revokeEvidence(RevokeEvidenceReq req) {
		Credential credential = req.getCredential();
		String hash = credential.obtainAllHash();
		TransactionResp<CredentialEvidence> resp = getCredentialContractService().queryCredentialEvience(hash);
		if(resp.checkFail()) {
			return BaseResp.build(resp.getCode(),resp.getErrMsg());
		}
		if(resp.getData() == null){
			return BaseResp.build(RetEnum.RET_EVIDENCE_NOT_EXIST_ERROR);
		}
		String evidenceSign = AlgorithmHandler.signMessageStr(hash, req.getPrivateKey());
		if(!evidenceSign.equals(resp.getData().getSignaturedata())){
			return BaseResp.build(RetEnum.RET_EVIDENCE_NO_OPERATION_ERROR);
		}
		TransactionResp<BigInteger> status = this.getCredentialContractService().getStatus(hash);
		if(status.checkFail()){
			return BaseResp.build(status.getCode(),status.getErrMsg());
		}
		if(CredentialStatus.checkFail(status.getData())){
			return BaseResp.build(RetEnum.RET_EVIDENCE_STATUS_INVALID);
		}
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
		String credentialHash = req.getCredential().obtainAllHash();
		//Query the data on the chain according to the hash calculated by credential
		QueryEvidenceReq queryEvidenceReq = QueryEvidenceReq.builder().evidenceId(credentialHash).build();
		BaseResp<QueryEvidenceResp> resp = this.queryEvidence(queryEvidenceReq);
		if (resp.checkFail()) {
			log.error("Find evidence data fail.");
			return BaseResp.build(resp.getCode(), resp.getErrMsg());
		}
		//Call the verification interface to verify the signature of the queried data
		VerifyEvidenceReq verifyEvidenceReq = VerifyEvidenceReq.builder().credentialHash(credentialHash)
				.evidenceSignInfo(resp.getData().getSignInfo()).publicKeyId(req.getCredential().obtainPublickeyId()).build();
		BaseResp<String> respEvidence = this.verifyEvidence(verifyEvidenceReq);
		if (respEvidence.checkFail()) {
			log.error("Verify evidence data fail.");
			return BaseResp.build(respEvidence.getCode(), respEvidence.getErrMsg());
		}
		return BaseResp.buildSuccess();
	}

}
