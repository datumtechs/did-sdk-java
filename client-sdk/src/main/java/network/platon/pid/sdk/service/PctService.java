package network.platon.pid.sdk.service;

import network.platon.pid.sdk.req.pct.CreatePctReq;
import network.platon.pid.sdk.req.pct.QueryPctInfoReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.pct.CreatePctResp;
import network.platon.pid.sdk.resp.pct.QueryPctInfoResp;

/**
 * Pct(PlatON Claim Template)  related method interface class
 * @Auther: Rongjin Zhang
 * @Date: 2020年5月28日
 * @Description:
 */
public interface PctService {

	/**
	 * Create pct data in PlatON
	 * @param req
	 * @return
	 */
	BaseResp<CreatePctResp> registerPct(CreatePctReq req);
	
	/**
	 * Query pct data according to request parameters in PlatON
	 * @param req
	 * @return
	 */
	BaseResp<QueryPctInfoResp> queryPctInfoById(QueryPctInfoReq req);
}
