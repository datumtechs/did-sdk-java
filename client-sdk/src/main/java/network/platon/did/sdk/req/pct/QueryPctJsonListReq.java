package network.platon.did.sdk.req.pct;

import lombok.*;
import network.platon.did.sdk.annoation.CustomNotBlank;
import network.platon.did.sdk.annoation.CustomPattern;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class QueryPctJsonListReq extends BaseReq {

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.DID_SIZE_MIN,
			max = ReqAnnoationArgs.DID_SIZE_MAX)
	@CustomPattern(value = DidConst.PLATON_DID_PATTERN)
	private String issuer;
}
