package network.platon.pid.sdk.req.agency;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.annoation.CustomPattern;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class RevocationAuthorityReq extends BaseReq{

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
}
