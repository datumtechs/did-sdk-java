package network.platon.did.sdk.resp.did;

import lombok.Data;
import network.platon.did.contract.dto.TransactionInfo;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-12 18:00
 */
@Data
public class SetDidAttrResp {

    private Boolean status;

    private TransactionInfo transactionInfo;
}
