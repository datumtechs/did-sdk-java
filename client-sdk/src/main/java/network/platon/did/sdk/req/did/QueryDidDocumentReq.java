package network.platon.did.sdk.req.did;

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
@Builder
@EqualsAndHashCode(callSuper=false)
public class QueryDidDocumentReq extends BaseReq{

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.DID_SIZE_MIN,
			max = ReqAnnoationArgs.DID_SIZE_MAX)
	@CustomPattern(value = DidConst.PLATONE_DID_PATTERN)
	private String did;
}
