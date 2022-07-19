package network.platon.pid.sdk.resp.agency;

import lombok.Data;

import java.math.BigInteger;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-15 14:36
 */
@Data
public class GetAdminResp {

    private String adminAddress;
    private String adminServiceUrl;
    private BigInteger adminJoinTime;

}
