package network.platon.pid.sdk.service.impl;

import com.platon.crypto.ECKeyPair;
import com.platon.utils.Numeric;
import lombok.Data;
import network.platon.pid.common.config.PidConfig;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import network.platon.pid.sdk.req.pct.QueryPctInfoReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.pct.CreatePctResp;
import network.platon.pid.sdk.resp.pct.QueryPctInfoResp;
import network.platon.pid.sdk.utils.PidUtils;
import org.junit.Test;

import network.platon.pid.sdk.BaseTest;
import network.platon.pid.sdk.req.pct.CreatePctReq;

import java.util.Random;

public class TestPctServiceImpl extends BaseTest {

    @Data
    class createPctResult {
        private String issuer;
        private BaseResp<CreatePctResp> createPctResp;
    }

    private createPctResult createPct () {

        ECKeyPair ecKeyPair = AlgorithmHandler.createEcKeyPair(PidConfig.getCONTRACT_PRIVATEKEY());
        String pidPublicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
        String pid = PidUtils.generatePid(pidPublicKey);

        String pctJson = "{\"properties\": { \"name\": { \"type\": \"string\" }, \"no\": { \"type\": \"string\" }, \"data\": { \"type\": \"string\" }}}";
        String str = "This is a String";
        CreatePctReq req = CreatePctReq.builder()
                .privateKey(PidConfig.getCONTRACT_PRIVATEKEY())
                .pctjson(pctJson)
                .extra(str.getBytes())
                .build();

        createPctResult res = new createPctResult();
        res.setIssuer(pid);
        res.setCreatePctResp(pctService.registerPct(req));
       return res;
    }

    @Test
    public void test_createPct() {
        // okResult(createPct().getCreatePctResp());
        Random randTest = new Random(23523865082340324L);
        System.out.println(randTest.nextLong());
        System.out.println(randTest.nextLong());
        System.out.println(randTest.nextLong());
        System.out.println(randTest.nextLong());
    }

    @Test
    public void test_queryPctJson() {
        BaseResp<CreatePctResp> createPctResp = createPct().getCreatePctResp();
        okResult(createPctResp);
        QueryPctInfoReq req = QueryPctInfoReq.builder()
        		.pctId(createPctResp.getData().getPctId()).build();
        BaseResp<QueryPctInfoResp> resp = pctService.queryPctInfoById(req);
        okResult(resp);
    }



}
