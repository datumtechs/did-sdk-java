package network.platon.pid.sdk.service.impl;

import lombok.Data;
import network.platon.pid.common.config.PidConfig;
import network.platon.pid.contract.dto.InitContractData;
import network.platon.pid.sdk.req.pct.QueryPctJsonListReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.agency.SetAuthorityResp;
import network.platon.pid.sdk.resp.pct.CreatePctResp;
import network.platon.pid.sdk.resp.pid.CreatePidResp;
import org.junit.Test;

import network.platon.pid.sdk.BaseTest;
import network.platon.pid.sdk.req.pct.CreatePctReq;
import network.platon.pid.sdk.req.pct.QueryPctJsonReq;

public class TestPctServiceImpl extends BaseTest {

    @Data
    class createPctResult {
        private String issuer;
        private BaseResp<CreatePctResp> createPctResp;
    }

    private createPctResult createPct () {
        BaseResp<CreatePidResp> createPidResp = this.createPidBase();
        if (createPidResp.checkFail()) {
            failedResult(createPidResp);
        }
        String pid = createPidResp.getData().getPid();
        BaseResp<SetAuthorityResp> setAuthorityRespBaseResp = this.addAuthorityByPid(pid);
        if (setAuthorityRespBaseResp.checkFail()) {
            failedResult(setAuthorityRespBaseResp);
        }
        ((PctServiceImpl)pctService).reloadContractData(new InitContractData(createPidResp.getData().getPrivateKey()));
        String pctJson = "{\"properties\": { \"name\": { \"type\": \"string\" }, \"no\": { \"type\": \"string\" }, \"data\": { \"type\": \"string\" }}}";
        CreatePctReq req = CreatePctReq.builder()
                .pid(pid)
                .pctjson(pctJson)
                .build();

        createPctResult res = new createPctResult();
        res.setIssuer(pid);
        res.setCreatePctResp(pctService.registerPct(req));
       return res;
    }

    @Test
    public void test_createPct() {
        okResult(createPct().getCreatePctResp());
    }

    @Test
    public void test_queryPctJson() {
        BaseResp<CreatePctResp> createPctResp = createPct().getCreatePctResp();
        okResult(createPctResp);
        QueryPctJsonReq req = QueryPctJsonReq.builder()
        		.pctId(createPctResp.getData().getPctId()).build();
        okResult(pctService.queryPctJsonById(req));
    }

    @Test
    public void test_queryPctIdsByPid() {
        createPctResult res = createPct();
        okResult(res.getCreatePctResp());
        QueryPctJsonListReq issuer = QueryPctJsonListReq.of(res.getIssuer());
        okResult(pctService.queryPctIdsByIssuer(issuer));
    }


}
