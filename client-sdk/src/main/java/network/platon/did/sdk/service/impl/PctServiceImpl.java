package network.platon.did.sdk.service.impl;

import com.platon.crypto.ECKeyPair;
import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.contract.dto.InitContractData;
import network.platon.did.csies.algorithm.AlgorithmHandler;
import network.platon.did.sdk.req.pct.CreatePctReq;
import network.platon.did.sdk.req.pct.QueryPctJsonReq;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.TransactionResp;
import network.platon.did.sdk.resp.pct.CreatePctResp;
import network.platon.did.sdk.resp.pct.QueryPctJsonResp;
import network.platon.did.sdk.service.BusinessBaseService;
import network.platon.did.sdk.service.PctService;
import network.platon.did.sdk.utils.PctUtils;
import network.platon.did.sdk.utils.DidUtils;

import java.io.Serializable;
import java.math.BigInteger;

@Slf4j
public class PctServiceImpl extends BusinessBaseService implements PctService,Serializable,Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5732175037207593062L;
	
	private static PctServiceImpl pctServiceImpl = new PctServiceImpl();
	
	public static PctServiceImpl getInstance(){
        try {
            return (PctServiceImpl) pctServiceImpl.clone();
        } catch (CloneNotSupportedException e) {
        	log.error("get instance error.", e);
        }
        return new PctServiceImpl();
    }
	
    @Override
    public BaseResp<CreatePctResp> registerPct(CreatePctReq req) {

        BaseResp<String> verifyBaseResp = req.validFiled();
        if (verifyBaseResp.getCode() != RetEnum.RET_SUCCESS.getCode()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
        }

        ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(req.getPrivateKey());
        String didPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
        String did = DidUtils.generateDid(didPublicKey);

        if (!PctUtils.isPctJsonSchemaValid(req.getPctjson())) {
            log.info("req did = {},json format error", did);
            return BaseResp.build(RetEnum.RET_PCT_JSON_SCHEMA_ERROR);
        }

        TransactionResp<BigInteger> tresp = this.getPctContractService(new InitContractData(req.getPrivateKey())).registerPct(req.getPctjson(), req.getExtra());
        if(tresp.checkFail()) {
        	log.error("Failed to register pctJson schema, req: {}, error:{}", req, tresp.getErrMsg());
			return BaseResp.build(tresp.getCode(), tresp.getErrMsg());
		}
        CreatePctResp res = new CreatePctResp();
        res.setPctId(tresp.getData().toString());
        res.setTransactionInfo(tresp.getTransactionInfo());
        return BaseResp.buildSuccess(res);
    }

    @Override
    public BaseResp<QueryPctJsonResp> queryPctJsonById(QueryPctJsonReq req) {
        BaseResp<String> verifyBaseResp = req.validFiled();
        if (verifyBaseResp.getCode() != RetEnum.RET_SUCCESS.getCode()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
        }
        BaseResp<String> pctJsonResp = this.getPctContractService().queryPctById(req.getPctId());
        if(!pctJsonResp.checkSuccess()) {
        	log.error("Failed to call queryPctJsonById, req: {}", req);
			return BaseResp.build(pctJsonResp.getCode(),pctJsonResp.getErrMsg());
		}
        return BaseResp.buildSuccess(QueryPctJsonResp.of(req.getPctId(), pctJsonResp.getData()));
    }
}
