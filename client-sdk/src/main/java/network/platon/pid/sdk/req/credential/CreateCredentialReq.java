package network.platon.pid.sdk.req.credential;

import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.*;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class CreateCredentialReq extends BaseReq {

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PID_SIZE_MIN,
			max = ReqAnnoationArgs.PID_SIZE_MAX)
	@CustomPattern(value = PidConst.PLATON_PID_PATTERN)
	private String pid;
	
	@CustomNotNull
	@CustomMin(value = ReqAnnoationArgs.COMMON_DATA_SIZE)
	private Long expirationDate;
	
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PCT_ID_SIZE_MIN)
	private String pctId;
	
	@CustomNotNull
	@CustomSize(min = ReqAnnoationArgs.COMMON_DATA_SIZE)
	private Map<String, Object> claim;
	
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PUBLIC_KEY_INDEX_SIZE_MIN)
	private String publicKeyId;
	
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
			max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
	@CustomPattern(value = PidConst.PLATON_PRIVATE_KEY_PATTERN)
	private String privateKey;

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PID_SIZE_MIN,
			max = ReqAnnoationArgs.PID_SIZE_MAX)
	@CustomPattern(value = PidConst.PLATON_PID_PATTERN)
	private String issuer;
	
	private String context;
	
	private String type;
}
