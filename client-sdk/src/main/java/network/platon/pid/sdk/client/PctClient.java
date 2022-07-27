package network.platon.pid.sdk.client;

import network.platon.pid.sdk.req.pct.CreatePctReq;
import network.platon.pid.sdk.req.pct.QueryPctInfoReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.pct.CreatePctResp;
import network.platon.pid.sdk.resp.pct.QueryPctInfoResp;
import network.platon.pid.sdk.service.PctService;

public class PctClient extends BusinessClient implements PctService  {

	@Override
	public BaseResp<CreatePctResp> registerPct(CreatePctReq req) {
		return this.getPctService().registerPct(req);
	}

	@Override
	public BaseResp<QueryPctInfoResp> queryPctInfoById(QueryPctInfoReq req) {
		return this.getPctService().queryPctInfoById(req);
	}
}
