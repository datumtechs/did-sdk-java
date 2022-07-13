package network.platon.pid.sdk.req.evidence;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.annoation.CustomNotNull;
import network.platon.pid.sdk.base.dto.Credential;
import network.platon.pid.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class RevokeEvidenceReq extends BaseReq {

	@CustomNotBlank
	private String privateKey;

	@CustomNotNull
	private Credential credential;

}
