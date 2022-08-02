package network.platon.did.sdk.req.evidence;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomNotBlank;
import network.platon.did.sdk.annoation.CustomNotNull;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.base.dto.EvidenceSignInfo;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class VerifyEvidenceReq extends BaseReq{

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
			max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
	private String credentialHash;
	
	@CustomNotNull
	private EvidenceSignInfo evidenceSignInfo;
	
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PUBLIC_KEY_INDEX_SIZE_MIN)
	private String publicKeyId;
	
}
