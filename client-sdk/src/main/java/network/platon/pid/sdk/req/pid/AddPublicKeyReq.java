package network.platon.pid.sdk.req.pid;

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
public class AddPublicKeyReq extends BaseReq{

	/**
	 * Required: The PlatON DID private key.
	 */
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
			max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
	@CustomPattern(value = PidConst.PLATON_PRIVATE_KEY_PATTERN)
	private String privateKey;

	/**
	 * Required: The public key.
	 */
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MIN,
			max = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MAX)
	@CustomPattern(value = PidConst.PLATON_PUBLICK_KEY_PATTERN)
	private String publicKey;

	/**
	 * Required: The type.  (default: Secp256k1)
	 */
	@Builder.Default
	@CustomIgnore
	private PidConst.PublicKeyType type = PidConst.PublicKeyType.SECP256K1;

	@CustomNotNull
	@CustomIgnore
	private int index;

	/**
	 * nothing to do.
	 * @param type the public key type
	 */
	public void setType(PidConst.PublicKeyType type) {
		this.type = type;
	}


}
