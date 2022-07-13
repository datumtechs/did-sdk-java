package network.platon.pid.sdk.resp.pid;

import lombok.Data;
import network.platon.pid.contract.dto.TransactionInfo;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-12 18:00
 */
@Data
public class SetPidAttrResp {

    private Boolean status;

    private TransactionInfo transactionInfo;
}
