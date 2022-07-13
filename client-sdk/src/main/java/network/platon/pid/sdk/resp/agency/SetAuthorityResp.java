package network.platon.pid.sdk.resp.agency;

import lombok.Data;
import network.platon.pid.contract.dto.TransactionInfo;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-15 14:38
 */
@Data
public class SetAuthorityResp {

    private boolean status;

    private TransactionInfo transactionInfo;
}
