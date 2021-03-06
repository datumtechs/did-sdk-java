package network.platon.pid.sdk.service;

import network.platon.pid.sdk.req.agency.*;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.agency.*;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-09 18:04
 */
public interface VoteService {

    BaseResp<Boolean> submitProposal(SubmitProposalReq req);

    BaseResp<Boolean> withdrawProposal(WithdrawProposalReq req);

    BaseResp<Boolean> voteProposal(VoteProposalReq req);

    BaseResp<Boolean> effectProposal(EffectProposalReq req);

    BaseResp<GetAdminResp> getAdmin();

    BaseResp<GetAllAuthorityResp> getAllAuthority();

    BaseResp<GetAllProposalIdResp> getAllProposalId();

    BaseResp<GetProposalIdResp> getProposalId(GetProposalIdReq req);

    BaseResp<GetProposalResp> getProposal(GetProposalReq req);
}
