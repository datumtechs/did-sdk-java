package network.platon.pid.sdk.req.presentation;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomNotNull;
import network.platon.pid.sdk.annoation.CustomPattern;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.base.dto.Challenge;
import network.platon.pid.sdk.base.dto.Presentation;
import network.platon.pid.sdk.base.dto.PresentationPolicy;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;


/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-05 17:23
 */
@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class VerifyPresetationReq extends BaseReq{

	@CustomNotNull
    @CustomSize(min = ReqAnnoationArgs.PID_SIZE_MIN,
            max = ReqAnnoationArgs.PID_SIZE_MAX)
    @CustomPattern(value = PidConst.PLATONE_PID_PATTERN)
    private String pid;

	@CustomNotNull
    private Presentation presentation;

	@CustomNotNull
    private Challenge challenge;

	@CustomNotNull
    private PresentationPolicy policy;

}
