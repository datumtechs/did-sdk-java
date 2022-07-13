package network.platon.pid.sdk.req.pid;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.*;
import network.platon.pid.sdk.base.dto.PidService;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class SetServiceReq extends BaseReq{

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PID_SIZE_MIN,
			max = ReqAnnoationArgs.PID_SIZE_MAX)
	@CustomPattern(value = PidConst.PLATONE_PID_PATTERN)
	private String pid;

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
			max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
	@CustomPattern(value = PidConst.PLATONE_PRIVATE_KEY_PATTERN)
	private String privateKey;

	@CustomNotNull
	@CustomIgnore
	private PidConst.DocumentAttrStatus status;
	
	@CustomNotNull
	private PidService service;

}
