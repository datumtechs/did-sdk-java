package network.platon.pid.sdk.req.pid;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.*;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

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
    private PidConst.DocumentStatus status;

    /**
     * nothing to do.
     * @param status the document status
     */
    public void setStatus(PidConst.DocumentStatus status) {
        this.status = status;
    }
}
