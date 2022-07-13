package network.platon.pid.sdk.resp.agency;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-17 17:17
 */
@Data
public class QueryAllAuthorityNameResp {
    private List<String> names;
}
