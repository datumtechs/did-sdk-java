package network.platon.pid.sdk.contract.service.impl;

import com.platon.crypto.Credentials;
import com.platon.parameters.NetworkParameters;
import com.platon.protocol.core.methods.response.TransactionReceipt;
import com.platon.tuples.generated.Tuple3;
import com.platon.tuples.generated.Tuple7;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.enums.RetEnum;

import network.platon.pid.contract.Vote;
import network.platon.pid.contract.dto.ContractNameValues;
import network.platon.pid.contract.dto.DeployContractData;
import network.platon.pid.contract.dto.TransactionInfo;
import network.platon.pid.sdk.base.dto.ProposalInfo;
import network.platon.pid.sdk.contract.service.VoteContractService;
import network.platon.pid.sdk.contract.service.ContractService;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.TransactionResp;
import network.platon.pid.sdk.resp.agency.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-15 10:55
 */
@Slf4j
public class VoteContractServiceImpl extends ContractService implements VoteContractService,Serializable,Cloneable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6963401261601796153L;

	private static VoteContractServiceImpl voteContractServiceImpl = new VoteContractServiceImpl();
	
	public static VoteContractServiceImpl getInstance(){
        try {
            return (VoteContractServiceImpl) voteContractServiceImpl.clone();
        } catch (CloneNotSupportedException e) {
        	log.error("get instance error.", e);
        }
        return new VoteContractServiceImpl();
    }

    @Override
    public BaseResp<Boolean> submitProposal(BigInteger proposalType, String proposalUrl, String candidate, String candidateServiceUrl){
        TransactionReceipt receipt = null;
        try {
            receipt = this.getVoteContract().submitProposal(proposalType, proposalUrl, candidate, candidateServiceUrl).send();
        } catch (Exception e) {
            log.error(
                    "Failed to call `submitProposal()` of vote contract, the proposalUrl: {}, the exception: {}",
                    proposalUrl, e
            );
            return TransactionResp.build(RetEnum.RET_VOTE_CALL_CONTRACT_ERROR);
        }

        return TransactionResp.buildTxSuccess(true, new TransactionInfo(receipt));
    }

    @Override
    public BaseResp<Boolean> withdrawProposal(BigInteger proposalId){
        TransactionReceipt receipt = null;
        try {
            receipt = this.getVoteContract().withdrawProposal(proposalId).send();
        } catch (Exception e) {
            log.error(
                    "Failed to call `withdrawProposal()` of vote contract, the proposalId: {}, the exception: {}",
                    proposalId, e
            );
            return TransactionResp.build(RetEnum.RET_VOTE_CALL_CONTRACT_ERROR);
        }

        return TransactionResp.buildTxSuccess(true, new TransactionInfo(receipt));
    }

    @Override
    public BaseResp<Boolean> voteProposal(BigInteger proposalId){
        TransactionReceipt receipt = null;
        try {
            receipt = this.getVoteContract().voteProposal(proposalId).send();
        } catch (Exception e) {
            log.error(
                    "Failed to call `voteProposal()` of vote contract, the proposalId: {}, the exception: {}",
                    proposalId, e
            );
            return TransactionResp.build(RetEnum.RET_VOTE_CALL_CONTRACT_ERROR);
        }

        return TransactionResp.buildTxSuccess(true, new TransactionInfo(receipt));
    }

    @Override
    public BaseResp<Boolean> effectProposal(BigInteger proposalId){
        TransactionReceipt receipt = null;
        try {
            receipt = this.getVoteContract().effectProposal(proposalId).send();
        } catch (Exception e) {
            log.error(
                    "Failed to call `effectProposal()` of vote contract, the proposalId: {}, the exception: {}",
                    proposalId, e
            );
            return TransactionResp.build(RetEnum.RET_VOTE_CALL_CONTRACT_ERROR);
        }

        return TransactionResp.buildTxSuccess(true, new TransactionInfo(receipt));
    }

    @Override
    public BaseResp<GetAdminResp> getAdmin(){
        Tuple3<String, String, BigInteger> result = null;
        try {
            result = this.getVoteContract().getAdmin().send();
        } catch (Exception e) {
            log.error(
                    "Failed to call `getAdmin()` on vote contract: the exception: {}",
                    e
            );
            return BaseResp.build(RetEnum.RET_VOTE_CALL_CONTRACT_ERROR);
        }

        GetAdminResp resp = new GetAdminResp();
        resp.setAdminAddress(result.getValue1());
        resp.setAdminServiceUrl(result.getValue2());
        resp.setAdminJoinTime(result.getValue3());

        return BaseResp.buildSuccess(resp);

    }

    @Override
    public BaseResp<GetAllAuthorityResp> getAllAuthority(){
        Tuple3<List<String>, List<String>, List<BigInteger>> result = null;
        try {
            result = this.getVoteContract().getAllAuthority().send();
        } catch (Exception e) {
            log.error(
                    "Failed to call `getAllAuthority()` on vote contract: the exception: {}",
                    e
            );
            return BaseResp.build(RetEnum.RET_VOTE_CALL_CONTRACT_ERROR);
        }

        GetAllAuthorityResp resp = new GetAllAuthorityResp();
        resp.setAddress(result.getValue1());
        resp.setServiceUrl(result.getValue2());
        resp.setJoinTime(result.getValue3());

        return BaseResp.buildSuccess(resp);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BaseResp<GetAllProposalIdResp> getAllProposalId(){
        List result = null;
        try {
            result = this.getVoteContract().getAllProposalId().send();
        } catch (Exception e) {
            log.error(
                    "Failed to call `getAllProposalId()` on vote contract: the exception: {}",
                    e
            );
            return BaseResp.build(RetEnum.RET_VOTE_CALL_CONTRACT_ERROR);
        }

        GetAllProposalIdResp resp = new GetAllProposalIdResp();
        resp.setProposalIds(result);

        return BaseResp.buildSuccess(resp);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BaseResp<GetProposalIdResp> getProposalId(BigInteger blockNo){
        List<String> result = null;
        try {
            result = this.getVoteContract().getProposalId(blockNo).send();
        } catch (Exception e) {
            log.error(
                    "Failed to call `getProposalId()` on vote contract blockNo: {}: the exception: {}", blockNo,
                    e
            );
            return BaseResp.build(RetEnum.RET_VOTE_CALL_CONTRACT_ERROR);
        }

        GetProposalIdResp resp = new GetProposalIdResp();
        resp.setProposalIds(result);

        return BaseResp.buildSuccess(resp);

    }

    @Override
    public BaseResp<GetProposalResp> getProposal(BigInteger proposalId){
        Tuple7<BigInteger, String, String, String, String, BigInteger, List<String>> result = null;
        try {
            result = this.getVoteContract().getProposal(proposalId).send();
        } catch (Exception e) {
            log.error(
                    "Failed to call `getProposalId()` on vote contract proposalId: {}: the exception: {}", proposalId,
                    e
            );
            return BaseResp.build(RetEnum.RET_VOTE_CALL_CONTRACT_ERROR);
        }

        ProposalInfo info = new ProposalInfo();
        info.setProposalType(result.getValue1());
        info.setProposalUrl(result.getValue2());
        info.setCandidate(result.getValue3());
        info.setCandidateServiceUrl(result.getValue4());
        info.setSubmitter(result.getValue5());
        info.setSubmitBlockNo(result.getValue6());
        info.setVoters(result.getValue7());

        GetProposalResp resp = new GetProposalResp();
        resp.setProposal(info);
        return BaseResp.buildSuccess(resp);
    }

	public TransactionResp<List<DeployContractData>> deployContract(Credentials credentials, String adminAddress, String serviceUrl) {
		try {
			Vote vote = Vote.deploy(getWeb3j(), credentials, gasProvider).send();
            TransactionReceipt receipt = vote.initialize(adminAddress, serviceUrl).send();
            if(!receipt.isStatusOK()){
                log.error("deployContract VoteContract error");
                return TransactionResp.build(RetEnum.RET_DEPLOY_CONTRACT_ERROR, "deployContract VoteContract error");
            }
            String voteTransHash = "";
            Optional<TransactionReceipt> value = vote.getTransactionReceipt();
            if(value.isPresent()){
                voteTransHash = value.get().getTransactionHash();
            }
			DeployContractData voteContractData = new DeployContractData(ContractNameValues.VOTE
					,vote.getContractAddress(), voteTransHash);

			List<DeployContractData> lists = Arrays.asList(voteContractData);
			return TransactionResp.buildSuccess(lists);
		} catch (Exception e) {
			log.error("deployContract VoteContract error", e);
			return TransactionResp.build(RetEnum.RET_DEPLOY_CONTRACT_ERROR, e.getMessage());
		}
	}
}
