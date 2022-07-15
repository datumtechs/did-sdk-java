package network.platon.pid.sdk.contract.service.impl;

import com.platon.crypto.Credentials;
import com.platon.protocol.core.methods.response.TransactionReceipt;
import com.platon.tuples.generated.Tuple2;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.common.utils.DateUtils;

import network.platon.pid.contract.dto.ContractNameValues;
import network.platon.pid.contract.dto.DeployContractData;
import network.platon.pid.contract.dto.TransactionInfo;
import network.platon.pid.sdk.base.dto.AuthorityInfo;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.contract.service.VoteContractService;
import network.platon.pid.sdk.contract.service.ContractService;
import network.platon.pid.sdk.req.agency.*;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.TransactionResp;
import network.platon.pid.sdk.resp.agency.*;
import network.platon.pid.sdk.utils.ConvertUtils;
import network.platon.pid.sdk.utils.PidUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
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
    public BaseResp<Boolean> submitProposal(SubmitProposalReq req){}

    @Override
    public BaseResp<Boolean> withdrawProposal(WithdrawProposalReq req){
    }

    @Override
    BaseResp<Boolean> voteProposal(VoteProposalReq req){}

    @Override
    BaseResp<Boolean> effectProposal(EffectProposalReq req){}

    @Override
    public BaseResp<GetAdminResp> getAdmin(){}

    @Override
    public BaseResp<GetAllAuthorityResp> getAllAuthority(){}

    @Override
    public BaseResp<GetAllProposalIdResp> getAllProposalId(){}

    @Override
    public BaseResp<GetProposalIdResp> getProposalId(GetProposalIdReq req){}

    @Override
    public BaseResp<GetProposalResp> getProposal(GetProposalReq req){}


    @Override
    public BaseResp<Tuple2<String, String>> getAdmin() {
        Tuple2<String, String> adminInfo = null;
        try {
            adminInfo = this.getVoteContract().getAdmin().send();
        } catch (Exception e) {
            log.error(
                    "Failed to call `getAdmin()` on vote contract: the exception: {}",
                    e
            );
            return BaseResp.build(RetEnum.RET_AGENCY_AUTHORITY_CALL_CONTRACT_ERROR);
        }
        return BaseResp.buildSuccess(adminInfo);
    }

	@Override
	public TransactionResp<List<DeployContractData>> deployContract(Credentials credentials, String contractAddress) {
		string string = new string(contractAddress);
		try {
			Vote vote = Vote.deploy(getWeb3j(), credentials, gasProvider, string).send();
            String authorityDataTransHash = "";
            Optional<TransactionReceipt> value = vote.getTransactionReceipt();
            if(value.isPresent()){
                voteTransHash = value.get().getTransactionHash();
            }
			DeployContractData voteContractData = new DeployContractData(ContractNameValues.VOTE
					,vote.getContractAddress(), voteTransHash);

			List<DeployContractData> lists = Arrays.asList(voteContractData);
			return TransactionResp.buildSuccess(lists);
		} catch (Exception e) {
			log.error("deployContract CredentialContract error", e);
			return TransactionResp.build(RetEnum.RET_DEPLOY_CONTRACT_ERROR, e.getMessage());
		}
	}
}
