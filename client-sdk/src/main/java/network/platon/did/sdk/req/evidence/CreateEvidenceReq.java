package network.platon.did.sdk.req.evidence;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomNotBlank;
import network.platon.did.sdk.annoation.CustomNotNull;
import network.platon.did.sdk.annoation.CustomPattern;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.base.dto.Credential;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class CreateEvidenceReq extends BaseReq{

	@CustomNotNull
	private Credential credential;
	
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
			max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
	@CustomPattern(value = DidConst.PLATONE_PRIVATE_KEY_PATTERN)
	private String privateKey;
	
}
