package network.platon.pid.sdk.req.agency;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.annoation.CustomNotNull;
import network.platon.pid.sdk.annoation.CustomPattern;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.base.dto.AuthorityInfo;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-15 14:55
 */
@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class SetAuthorityReq extends BaseReq{

    @CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
            max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
    @CustomPattern(value = PidConst.PLATONE_PRIVATE_KEY_PATTERN)
    private String privateKey;

    @CustomNotNull
    private AuthorityInfo authority;

}
