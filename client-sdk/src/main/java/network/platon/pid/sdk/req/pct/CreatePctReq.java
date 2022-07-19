package network.platon.pid.sdk.req.pct;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.annoation.CustomPattern;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
public class CreatePctReq extends BaseReq{

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
			max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
	@CustomPattern(value = PidConst.PLATONE_PRIVATE_KEY_PATTERN)
	private String privateKey;
	
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PCT_JSON_SIZE_MIN)
	private String pctjson;

	private byte[] extra;
}
