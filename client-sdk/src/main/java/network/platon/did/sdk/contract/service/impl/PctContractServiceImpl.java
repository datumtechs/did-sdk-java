package network.platon.did.sdk.contract.service.impl;

import com.platon.crypto.Credentials;
import com.platon.protocol.core.methods.response.TransactionReceipt;
import com.platon.tuples.generated.Tuple3;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.contract.Pct;
import network.platon.did.contract.dto.ContractNameValues;
import network.platon.did.contract.dto.DeployContractData;
import network.platon.did.contract.dto.TransactionInfo;
import network.platon.did.sdk.base.dto.PctData;
import network.platon.did.sdk.contract.service.ContractService;
import network.platon.did.sdk.contract.service.PctContractService;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.TransactionResp;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author yzw
 */
@Slf4j
public class PctContractServiceImpl extends ContractService implements PctContractService,Serializable,Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6963401261601796153L;

	private static PctContractServiceImpl pctContractServiceImpl = new PctContractServiceImpl();
	
	public static PctContractServiceImpl getInstance(){
        try {
            return (PctContractServiceImpl) pctContractServiceImpl.clone();
        } catch (CloneNotSupportedException e) {
        	log.error("get instance error.", e);
        }
        return new PctContractServiceImpl();
    }
	
	@Override
	public TransactionResp<BigInteger> registerPct(String pctJson, byte[] extra) {
		try {
			TransactionReceipt receipt = this.getPctContract().registerPct(pctJson, extra).send();

			List<Pct.RegisterPctEventResponse> registerPctEventResponses = this.getPctContract().getRegisterPctEvents(receipt);
			BigInteger arg1 = registerPctEventResponses.get(0).pctId;
			return TransactionResp.buildTxSuccess(arg1, new TransactionInfo(receipt));
		} catch (Exception e) {
			log.error("registerPct pct error", e);
			return TransactionResp.build(RetEnum.RET_PCT_REGISTER_CNTRACNT_ERROR,e.getMessage());
		}
	}

	@Override
	public BaseResp<PctData> queryPctById(String pctId) {
		try {
			Tuple3<String, String, byte[]> pctInfo = this.getPctContract().getPctInfo(new BigInteger(pctId)).send();
			if (StringUtils.isBlank(pctInfo.getValue2())) {
				return BaseResp.build(RetEnum.RET_PCT_QUERY_JSON_NOT_FOUND_ERROR);
			}
			PctData result = new PctData();
			result.setPctId(pctId);
			result.setIssue(pctInfo.getValue1());
			result.setPctJson(pctInfo.getValue2());
			result.setExtra(pctInfo.getValue3());
			return BaseResp.buildSuccess(result);
		} catch (Exception e) {
			log.error("queryPctJsonById pct error {}", pctId, e);
			return BaseResp.build(RetEnum.RET_PCT_QUERY_BY_ID_ERROR,e.getMessage());
		}
	}

	public TransactionResp<List<DeployContractData>> deployContract(Credentials credentials, String voteContractAddress) {
		try {
			Pct pct = Pct.deploy(getWeb3j(), credentials, gasProvider).send();
			TransactionReceipt receipt = pct.initialize(voteContractAddress).send();
			if(!receipt.isStatusOK()){
				log.error("deployContract PctContract error");
				return TransactionResp.build(RetEnum.RET_DEPLOY_CONTRACT_ERROR, "deployContract PctContract error");
			}
			Optional<TransactionReceipt> value = pct.getTransactionReceipt();
			String pctTransHash = "";
			if(value.isPresent()){
				pctTransHash = value.get().getTransactionHash();
			}
			DeployContractData pctContractData = new DeployContractData(ContractNameValues.PCT
					,pct.getContractAddress(),pctTransHash);
			List<DeployContractData> lists = Arrays.asList(pctContractData);
			return TransactionResp.buildSuccess(lists);
		} catch (Exception e) {
			log.error("deployContract PctContract error", e);
			return TransactionResp.build(RetEnum.RET_DEPLOY_CONTRACT_ERROR, e.getMessage());
		}
	}

}
