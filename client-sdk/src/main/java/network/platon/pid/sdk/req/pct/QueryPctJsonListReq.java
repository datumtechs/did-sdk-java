package network.platon.pid.sdk.req.pct;

import lombok.*;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.annoation.CustomPattern;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class QueryPctJsonListReq extends BaseReq {

    @CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PID_SIZE_MIN,
            max = ReqAnnoationArgs.PID_SIZE_MAX)
    @CustomPattern(value = PidConst.PLATONE_PID_PATTERN)
    private String issuer;
}
