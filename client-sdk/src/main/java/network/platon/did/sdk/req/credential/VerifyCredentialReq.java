package network.platon.did.sdk.req.credential;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomNotNull;
import network.platon.did.sdk.base.dto.Credential;
import network.platon.did.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class VerifyCredentialReq extends BaseReq{

	@CustomNotNull
	private Credential credential;
}
