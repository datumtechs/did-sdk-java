package network.platon.did.sdk.client;

import network.platon.did.sdk.req.pct.CreatePctReq;
import network.platon.did.sdk.req.pct.QueryPctInfoReq;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.pct.CreatePctResp;
import network.platon.did.sdk.resp.pct.QueryPctInfoResp;
import network.platon.did.sdk.service.PctService;

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
