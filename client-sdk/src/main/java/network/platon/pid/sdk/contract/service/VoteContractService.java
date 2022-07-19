package network.platon.pid.sdk.contract.service;

import com.platon.tuples.generated.Tuple2;
import network.platon.pid.sdk.req.agency.*;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.agency.*;

import java.math.BigInteger;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-15 10:54
 */
public interface VoteContractService {

    BaseResp<Boolean> submitProposal(BigInteger proposalType,  String proposalUrl, String candidate, String candidateServiceUrl);

    BaseResp<Boolean> withdrawProposal(BigInteger proposalId);

    BaseResp<Boolean> voteProposal(BigInteger proposalId);

    BaseResp<Boolean> effectProposal(BigInteger proposalId);

    BaseResp<GetAdminResp> getAdmin();

    BaseResp<GetAllAuthorityResp> getAllAuthority();

    BaseResp<GetAllProposalIdResp> getAllProposalId();

    BaseResp<GetProposalIdResp> getProposalId(BigInteger blockNo);

    BaseResp<GetProposalResp> getProposal(BigInteger proposalId);

}
