package network.platon.pid.sdk.client;

import java.util.List;

import network.platon.pid.sdk.req.pct.CreatePctReq;
import network.platon.pid.sdk.req.pct.QueryPctJsonListReq;
import network.platon.pid.sdk.req.pct.QueryPctJsonReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.pct.CreatePctResp;
import network.platon.pid.sdk.resp.pct.QueryPctJsonResp;
import network.platon.pid.sdk.service.PctService;

public class PctClient extends BusinessClient implements PctService  {

	@Override
	public BaseResp<CreatePctResp> registerPct(CreatePctReq req) {
		return this.getPctService().registerPct(req);
	}

	@Override
	public BaseResp<QueryPctJsonResp> queryPctJsonById(QueryPctJsonReq req) {
		return this.getPctService().queryPctJsonById(req);
	}
}
