package network.platon.pid.sdk.req.credential;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomNotNull;
import network.platon.pid.sdk.base.dto.Credential;
import network.platon.pid.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class VerifyCredentialReq extends BaseReq{

	@CustomNotNull
	private Credential credential;
}
