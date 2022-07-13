package network.platon.pid.sdk.base.dto;

import lombok.Data;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.annoation.CustomPattern;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;

import java.math.BigInteger;
import java.util.Map;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-18 10:28
 */
@Data
public class AuthorityInfo {

    /**
     * Required: The PlatON DID.
     */
    @CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PID_SIZE_MIN,
            max = ReqAnnoationArgs.PID_SIZE_MAX)
    @CustomPattern(value = PidConst.PLATONE_PID_PATTERN)
    private String pid;

    /**
     * Required: The authority name.
     */
    @CustomNotBlank
    private String name;

    /**
     * Required: The create date.
     * This datetime value MUST be normalized to UTC 00:00, as indicated by the trailing "Z".
     * e.g. "2020-06-03T17:10:00Z"
     */
    private String createTime;

    private BigInteger accumulate;

    private Map<String, Object> extra;
}
