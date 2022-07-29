package network.platon.pid.sdk.service.impl;

import com.platon.crypto.ECKeyPair;
import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.contract.dto.InitContractData;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import network.platon.pid.sdk.base.dto.PctData;
import network.platon.pid.sdk.req.pct.CreatePctReq;
import network.platon.pid.sdk.req.pct.QueryPctInfoReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.TransactionResp;
import network.platon.pid.sdk.resp.pct.CreatePctResp;
import network.platon.pid.sdk.resp.pct.QueryPctInfoResp;
import network.platon.pid.sdk.service.BusinessBaseService;
import network.platon.pid.sdk.service.PctService;
import network.platon.pid.sdk.utils.PctUtils;
import network.platon.pid.sdk.utils.PidUtils;

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
        String pidPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
        String pid = PidUtils.generatePid(pidPublicKey);

        if (!PctUtils.isPctJsonSchemaValid(req.getPctjson())) {
            log.info("req pid = {},json format error", pid);
            return BaseResp.build(RetEnum.RET_PCT_JSON_SCHEMA_ERROR);
        }

        this.ChangePrivateKey(req.getPrivateKey());
        TransactionResp<BigInteger> tresp = this.getPctContractService().registerPct(req.getPctjson(), req.getExtra());
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
    public BaseResp<QueryPctInfoResp> queryPctInfoById(QueryPctInfoReq req) {
        BaseResp<String> verifyBaseResp = req.validFiled();
        if (verifyBaseResp.getCode() != RetEnum.RET_SUCCESS.getCode()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
        }
        BaseResp<PctData> pctInfoResp = this.getPctContractService().queryPctById(req.getPctId());
        if(!pctInfoResp.checkSuccess()) {
        	log.error("Failed to call queryPctJsonById, req: {}", req);
			return BaseResp.build(pctInfoResp.getCode(),pctInfoResp.getErrMsg());
		}
        return BaseResp.buildSuccess(QueryPctInfoResp.of(pctInfoResp.getData()));
    }
}
