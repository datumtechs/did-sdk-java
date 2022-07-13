package network.platon.pid.sdk.req.credential;

import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomNotNull;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.base.dto.Credential;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

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
