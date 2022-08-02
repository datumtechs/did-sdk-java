package network.platon.did.sdk.req.evidence;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomNotNull;
import network.platon.did.sdk.base.dto.Credential;
import network.platon.did.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class VerifyCredentialEvidenceReq extends BaseReq{

	@CustomNotNull
	private Credential credential;
	
}
