package network.platon.did.sdk.req.did;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.*;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-08 15:52
 */
@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class SetDidAuthReq extends BaseReq {

	@CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.DID_SIZE_MIN,
            max = ReqAnnoationArgs.DID_SIZE_MAX)
    @CustomPattern(value = DidConst.PLATON_DID_PATTERN)
    private String did;

	@CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.DID_SIZE_MIN,
            max = ReqAnnoationArgs.DID_SIZE_MAX)
    @CustomPattern(value = DidConst.PLATON_DID_PATTERN)
    private String controller;

	@CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
            max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
    @CustomPattern(value = DidConst.PLATON_PRIVATE_KEY_PATTERN)
    private String privateKey;

	@CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MIN,
            max = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MAX)
    @CustomPattern(value = DidConst.PLATON_PUBLICK_KEY_PATTERN)
    private String publicKey;

	@CustomNotNull
	@CustomIgnore
    private DidConst.DocumentAttrStatus status;

    /**
     * nothing to do.
     * @param status the authentication status
     */
    public void setStatus(DidConst.DocumentAttrStatus status) {
        this.status = status;
    }

}
