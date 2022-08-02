package network.platon.did.sdk.resp.pct;

import lombok.Data;
import network.platon.did.contract.dto.TransactionInfo;

@Data
public class CreatePctResp {

    private String pctId;

    private TransactionInfo transactionInfo;
}
