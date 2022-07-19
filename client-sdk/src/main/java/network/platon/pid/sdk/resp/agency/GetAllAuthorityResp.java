package network.platon.pid.sdk.resp.agency;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class GetAllAuthorityResp {

    private List<String> address;
    private List<String> serviceUrl;
    private List<BigInteger> joinTime;
}
