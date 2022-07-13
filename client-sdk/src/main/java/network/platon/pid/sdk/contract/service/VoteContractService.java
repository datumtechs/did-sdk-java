package network.platon.pid.sdk.contract.service;

import com.platon.tuples.generated.Tuple2;
import network.platon.pid.sdk.base.dto.AuthorityInfo;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.TransactionResp;

import java.math.BigInteger;
import java.util.List;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-15 10:54
 */
public interface VoteContractService {

    /**
     * Get the associated RoleContract address in AuthorityContract
     * @return
     */
    BaseResp<Tuple2<String, String>> getAdmin();

}
