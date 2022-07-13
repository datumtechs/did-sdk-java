package network.platon.pid.sdk.req.evidence;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomNotNull;
import network.platon.pid.sdk.base.dto.Credential;
import network.platon.pid.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class VerifyCredentialEvidenceReq extends BaseReq{

	@CustomNotNull
	private Credential credential;
	
}
