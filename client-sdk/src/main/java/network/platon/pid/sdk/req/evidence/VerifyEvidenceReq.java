package network.platon.pid.sdk.req.evidence;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.annoation.CustomNotNull;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.base.dto.EvidenceSignInfo;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

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
