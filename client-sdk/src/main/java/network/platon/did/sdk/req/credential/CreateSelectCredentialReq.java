package network.platon.did.sdk.req.credential;

import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomNotNull;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.base.dto.Credential;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class CreateSelectCredentialReq extends BaseReq {

	@CustomNotNull
	private Credential credential;

	@CustomNotNull
	@CustomSize(min = ReqAnnoationArgs.SELECTMAP_SIZE_MIN)
	private Map<String, Object> selectMap;
}
