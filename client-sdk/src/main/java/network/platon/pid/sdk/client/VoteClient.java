package network.platon.pid.sdk.client;

import network.platon.pid.sdk.req.agency.*;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.agency.*;
import network.platon.pid.sdk.service.VoteService;

public class VoteClient extends BusinessClient implements VoteService {

	@Override
	public BaseResp<Boolean> submitProposal(SubmitProposalReq req){
		return getVoteService().submitProposal(req);
	}

	@Override
	public BaseResp<Boolean> withdrawProposal(WithdrawProposalReq req){
		return getVoteService().withdrawProposal(req);
	}

	@Override
	public BaseResp<Boolean> voteProposal(VoteProposalReq req){
		return getVoteService().voteProposal(req);
	}

	@Override
	public BaseResp<Boolean> effectProposal(EffectProposalReq req){
		return getVoteService().effectProposal(req);
	}

	@Override
	public BaseResp<GetAdminResp> getAdmin(){
		return getVoteService().getAdmin();
	}

	@Override
	public BaseResp<GetAllAuthorityResp> getAllAuthority(){
		return getVoteService().getAllAuthority();
	}

	@Override
	public BaseResp<GetAllProposalIdResp> getAllProposalId(){
		return getVoteService().getAllProposalId();
	}

	@Override
	public BaseResp<GetProposalIdResp> getProposalId(GetProposalIdReq req){
		return getVoteService().getProposalId(req);
	}

	@Override
	public BaseResp<GetProposalResp> getProposal(GetProposalReq req){
		return getVoteService().getProposal(req);
	}
}

