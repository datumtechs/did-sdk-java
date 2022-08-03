package network.platon.did.sdk.req.pct;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomNotBlank;
import network.platon.did.sdk.annoation.CustomPattern;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
public class CreatePctReq extends BaseReq{

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
			max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
	@CustomPattern(value = DidConst.PLATON_PRIVATE_KEY_PATTERN)
	private String privateKey;
	
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PCT_JSON_SIZE_MIN)
	private String pctjson;

	private byte[] extra;
}
