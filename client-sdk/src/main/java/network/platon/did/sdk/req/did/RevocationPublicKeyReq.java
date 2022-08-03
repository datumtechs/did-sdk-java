package network.platon.did.sdk.req.did;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomNotBlank;
import network.platon.did.sdk.annoation.CustomPattern;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class RevocationPublicKeyReq extends BaseReq{

	/**
	 * Required: The PlatON DID private key.
	 */
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
			max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
	@CustomPattern(value = DidConst.PLATON_PRIVATE_KEY_PATTERN)
	private String privateKey;

	/**
	 * Required: The public key.
	 */
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MIN,
			max = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MAX)
	@CustomPattern(value = DidConst.PLATON_PUBLICK_KEY_PATTERN)
	private String publicKey;
	
}
