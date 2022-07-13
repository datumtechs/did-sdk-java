package network.platon.pid.sdk.resp.agency;

import lombok.Data;
import network.platon.pid.sdk.base.dto.AuthorityInfo;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-15 14:40
 */
@Data
public class QueryAuthorityResp {

    private boolean status;

    private AuthorityInfo authorityInfo;
}
