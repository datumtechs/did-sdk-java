package network.platon.did.sdk.service;

import network.platon.did.sdk.req.agency.*;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.agency.*;

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
