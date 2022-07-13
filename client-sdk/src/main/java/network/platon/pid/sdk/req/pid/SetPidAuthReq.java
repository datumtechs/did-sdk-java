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
 * @Date: 2020-06-08 15:52
 */
@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class SetPidAuthReq extends BaseReq {

	@CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PID_SIZE_MIN,
            max = ReqAnnoationArgs.PID_SIZE_MAX)
    @CustomPattern(value = PidConst.PLATONE_PID_PATTERN)
    private String pid;

	@CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PID_SIZE_MIN,
            max = ReqAnnoationArgs.PID_SIZE_MAX)
    @CustomPattern(value = PidConst.PLATONE_PID_PATTERN)
    private String controller;

	@CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
            max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
    @CustomPattern(value = PidConst.PLATONE_PRIVATE_KEY_PATTERN)
    private String privateKey;

	@CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MIN,
            max = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MAX)
    @CustomPattern(value = PidConst.PLATONE_PUBLICK_KEY_PATTERN)
    private String publicKey;

	@CustomNotNull
	@CustomIgnore
    private PidConst.DocumentAttrStatus status;

    /**
     * nothing to do.
     * @param status the authentication status
     */
    public void setStatus(PidConst.DocumentAttrStatus status) {
        this.status = status;
    }

}
