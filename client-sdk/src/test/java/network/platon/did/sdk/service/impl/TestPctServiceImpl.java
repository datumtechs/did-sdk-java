package network.platon.did.sdk.service.impl;

import lombok.Data;
import network.platon.did.contract.dto.InitContractData;
import network.platon.did.sdk.req.pct.QueryPctJsonListReq;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.pct.CreatePctResp;
import network.platon.did.sdk.resp.did.CreateDidResp;
import org.junit.Test;

import network.platon.did.sdk.BaseTest;
import network.platon.did.sdk.req.pct.CreatePctReq;
import network.platon.did.sdk.req.pct.QueryPctJsonReq;

public class TestPctServiceImpl extends BaseTest {

    @Data
    class createPctResult {
        private String issuer;
        private BaseResp<CreatePctResp> createPctResp;
    }

    private createPctResult createPct () {
        BaseResp<CreateDidResp> createDidResp = this.createDidBase();
        if (createDidResp.checkFail()) {
            failedResult(createDidResp);
        }
        String did = createDidResp.getData().getDid();
        ((PctServiceImpl)pctService).reloadContractData(new InitContractData(createDidResp.getData().getPrivateKey()));
        String pctJson = "{\"properties\": { \"name\": { \"type\": \"string\" }, \"no\": { \"type\": \"string\" }, \"data\": { \"type\": \"string\" }}}";
        CreatePctReq req = CreatePctReq.builder()
                .pctjson(pctJson)
                .build();

        createPctResult res = new createPctResult();
        res.setIssuer(did);
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
    public void test_queryPctIdsByDid() {
        createPctResult res = createPct();
        okResult(res.getCreatePctResp());
        QueryPctJsonListReq issuer = QueryPctJsonListReq.of(res.getIssuer());
    }


}
