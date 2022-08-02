package network.platon.did.sdk.service.impl;

import lombok.extern.slf4j.Slf4j;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.contract.dto.InitContractData;
import network.platon.did.sdk.req.agency.*;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.TransactionResp;
import network.platon.did.sdk.resp.agency.*;
import network.platon.did.sdk.service.VoteService;
import network.platon.did.sdk.service.BusinessBaseService;

import java.io.Serializable;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-09 18:04
 */
@Slf4j
public class VoteServiceImpl extends BusinessBaseService implements VoteService,Serializable,Cloneable {

	private static final long serialVersionUID = 5732175037207593062L;
	
	private static VoteServiceImpl agencyService = new VoteServiceImpl();
	
	public static VoteServiceImpl getInstance(){
        try {
            return (VoteServiceImpl) agencyService.clone();
        } catch (CloneNotSupportedException e) {
        	log.error("get instance error.", e);
        }
        return new VoteServiceImpl();
    }

    @Override
    public BaseResp<Boolean> submitProposal(SubmitProposalReq req){
        BaseResp<String> verifyBaseResp = req.validFiled();
        if (verifyBaseResp.checkFail()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
        }

        TransactionResp<Boolean> resp =
                (TransactionResp<Boolean>) this.getVoteContractService(new InitContractData(req.getPrivateKey()))
                        .submitProposal(req.getProposalType(), req.getProposalUrl(), req.getCandidate(), req.getCandidateServiceUrl());

        return resp;
    }

    @Override
    public BaseResp<Boolean> withdrawProposal(WithdrawProposalReq req){
        BaseResp<String> verifyBaseResp = req.validFiled();
        if (verifyBaseResp.checkFail()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
        }

        TransactionResp<Boolean> resp =
                (TransactionResp<Boolean>) this.getVoteContractService(new InitContractData(req.getPrivateKey()))
                        .withdrawProposal(req.getProposalId());

        return resp;
    }

    @Override
    public BaseResp<Boolean> voteProposal(VoteProposalReq req){
        BaseResp<String> verifyBaseResp = req.validFiled();
        if (verifyBaseResp.checkFail()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
        }

        TransactionResp<Boolean> resp =
                (TransactionResp<Boolean>) this.getVoteContractService(new InitContractData(req.getPrivateKey()))
                        .voteProposal(req.getProposalId());

        return resp;
    }

    @Override
    public BaseResp<Boolean> effectProposal(EffectProposalReq req){
        BaseResp<String> verifyBaseResp = req.validFiled();
        if (verifyBaseResp.checkFail()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
        }

        TransactionResp<Boolean> resp =
                (TransactionResp<Boolean>) this.getVoteContractService(new InitContractData(req.getPrivateKey()))
                        .effectProposal(req.getProposalId());

        return resp;
    }

    @Override
    public BaseResp<GetAdminResp> getAdmin(){
        BaseResp<GetAdminResp> resp = this.getVoteContractService().getAdmin();
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }

        return resp;
    }

    @Override
    public BaseResp<GetAllAuthorityResp> getAllAuthority(){
        BaseResp<GetAllAuthorityResp> resp = this.getVoteContractService().getAllAuthority();
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }

        return resp;
    }

    @Override
    public BaseResp<GetAllProposalIdResp> getAllProposalId(){
        BaseResp<GetAllProposalIdResp> resp = this.getVoteContractService().getAllProposalId();
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }

        return resp;
    }

    @Override
    public BaseResp<GetProposalIdResp> getProposalId(GetProposalIdReq req){
        BaseResp<String> verifyBaseResp = req.validFiled();
        if (verifyBaseResp.checkFail()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
        }

        BaseResp<GetProposalIdResp> resp = this.getVoteContractService().getProposalId(req.getBlockNo());
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }

        return resp;
    }

    @Override
    public BaseResp<GetProposalResp> getProposal(GetProposalReq req){
        BaseResp<String> verifyBaseResp = req.validFiled();
        if (verifyBaseResp.checkFail()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
        }

        BaseResp<GetProposalResp> resp = this.getVoteContractService().getProposal(req.getProposalId());
        if (resp.checkFail()) {
            return BaseResp.build(resp.getCode(), resp.getErrMsg());
        }

        return resp;
    }
}
