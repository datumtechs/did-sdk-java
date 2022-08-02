package network.platon.did.sdk.req.evidence;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomNotBlank;
import network.platon.did.sdk.annoation.CustomNotNull;
import network.platon.did.sdk.base.dto.Credential;
import network.platon.did.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class RevokeEvidenceReq extends BaseReq {

	@CustomNotBlank
	private String privateKey;

	@CustomNotNull
	private Credential credential;

}
