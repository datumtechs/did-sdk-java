package network.platon.did.sdk.req.credential;

import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.*;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class CreateCredentialReq extends BaseReq {

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.DID_SIZE_MIN,
			max = ReqAnnoationArgs.DID_SIZE_MAX)
	@CustomPattern(value = DidConst.PLATON_DID_PATTERN)
	private String did;
	
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
	@CustomPattern(value = DidConst.PLATON_PRIVATE_KEY_PATTERN)
	private String privateKey;

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.DID_SIZE_MIN,
			max = ReqAnnoationArgs.DID_SIZE_MAX)
	@CustomPattern(value = DidConst.PLATON_DID_PATTERN)
	private String issuer;
	
	private String context;
	
	private String type;
}
