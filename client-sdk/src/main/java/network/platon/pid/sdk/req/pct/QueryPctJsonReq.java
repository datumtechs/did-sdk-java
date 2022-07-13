package network.platon.pid.sdk.req.pct;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
public class QueryPctJsonReq extends BaseReq {

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PCT_ID_SIZE_MIN)
	private String pctId;
}
