package network.platon.pid.sdk.service.impl;

import lombok.extern.slf4j.Slf4j;
import network.platon.pid.common.enums.RetEnum;
import network.platon.pid.sdk.req.pct.CreatePctReq;
import network.platon.pid.sdk.req.pct.QueryPctJsonListReq;
import network.platon.pid.sdk.req.pct.QueryPctJsonReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.TransactionResp;
import network.platon.pid.sdk.resp.pct.CreatePctResp;
import network.platon.pid.sdk.resp.pct.QueryPctJsonResp;
import network.platon.pid.sdk.service.BusinessBaseService;
import network.platon.pid.sdk.service.PctService;
import network.platon.pid.sdk.utils.PctUtils;

import java.io.Serializable;
import java.util.List;

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

        if (!PctUtils.isPctJsonSchemaValid(req.getPctjson())) {
            log.info("req pid = {},json format error", req.getPid());
            return BaseResp.build(RetEnum.RET_PCT_JSON_SCHEMA_ERROR);
        }
        TransactionResp<Uint32> tresp = this.getPctContractService().registerPct(req.getPctjson(), req.getPid());
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
        BaseResp<String> pctJsonResp = this.getPctContractService().queryPctJsonById(req.getPctId());
        if(!pctJsonResp.checkSuccess()) {
        	log.error("Failed to call queryPctJsonById, req: {}", req);
			return BaseResp.build(pctJsonResp.getCode(),pctJsonResp.getErrMsg());
		}
        BaseResp<String> pidResp = this.getPctContractService().queryPctIssuerById(req.getPctId());
        if(!pidResp.checkSuccess()) {
            log.error("Failed to call queryPctJsonById, req: {}", req);
            return BaseResp.build(pidResp.getCode(),pidResp.getErrMsg());
        }
        return BaseResp.buildSuccess(QueryPctJsonResp.of(pidResp.getData(), pctJsonResp.getData()));
    }

    @Override
    public BaseResp<List<Integer>> queryPctIdsByIssuer(QueryPctJsonListReq req) {
        BaseResp<String> verifyBaseResp = req.validFiled();
        if (verifyBaseResp.getCode() != RetEnum.RET_SUCCESS.getCode()) {
            return BaseResp.build(RetEnum.RET_COMMON_PARAM_INVALLID, verifyBaseResp.getData());
        }
        BaseResp<List<Integer>> resp = this.getPctContractService().queryPctIdsByIssuer(req.getIssuer());
        if(!resp.checkSuccess()) {
        	log.error("Failed to call queryPctIdsByIssuer, req: {}", req);
			return BaseResp.build(resp.getCode(),resp.getErrMsg());
		}
        return BaseResp.buildSuccess(resp.getData());
    }
}
