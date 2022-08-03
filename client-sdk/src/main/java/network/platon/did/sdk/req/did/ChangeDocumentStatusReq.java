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
 * @Date: 2020-06-17 10:42
 */
@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class ChangeDocumentStatusReq extends BaseReq {
	@CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
            max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
    @CustomPattern(value = DidConst.PLATON_PRIVATE_KEY_PATTERN)
    private String privateKey;

	@CustomNotNull
	@CustomIgnore
    private DidConst.DocumentStatus status;

    /**
     * nothing to do.
     * @param status the document status
     */
    public void setStatus(DidConst.DocumentStatus status) {
        this.status = status;
    }
}
