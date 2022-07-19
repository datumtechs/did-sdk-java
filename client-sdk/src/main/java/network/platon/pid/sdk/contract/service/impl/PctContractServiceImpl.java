package network.platon.pid.sdk.contract.service.impl;

import com.platon.crypto.Credentials;
import com.platon.protocol.core.methods.response.TransactionReceipt;
import com.platon.tuples.generated.Tuple3;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.contract.Pct;
import network.platon.pid.contract.dto.ContractNameValues;
import network.platon.pid.contract.dto.DeployContractData;
import network.platon.pid.contract.dto.TransactionInfo;
import network.platon.pid.sdk.contract.service.ContractService;
import network.platon.pid.sdk.contract.service.PctContractService;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.TransactionResp;

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
	public BaseResp<String> queryPctById(String pctId) {
		try {
			Tuple3<String, String, byte[]> pctInfo = this.getPctContract().getPctInfo(new BigInteger(pctId)).send();
			String pctJson = new String(pctInfo.getValue2());
			if (StringUtils.isBlank(pctJson)) {
				return BaseResp.build(RetEnum.RET_PCT_QUERY_JSON_NOT_FOUND_ERROR);
			}
			return BaseResp.buildSuccess(pctJson);
		} catch (Exception e) {
			log.error("queryPctJsonById pct error {}", pctId, e);
			return BaseResp.build(RetEnum.RET_PCT_QUERY_BY_ID_ERROR,e.getMessage());
		}
	}

	@Override
	public TransactionResp<List<DeployContractData>> deployContract(Credentials credentials, String contractAddress) {
		String string = new String(contractAddress);
		try {
			Pct pct = Pct.deploy(getWeb3j(), credentials, gasProvider)
					.send();
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
			log.error("deployContract CredentialContract error", e);
			return TransactionResp.build(RetEnum.RET_DEPLOY_CONTRACT_ERROR, e.getMessage());
		}
	}

}
