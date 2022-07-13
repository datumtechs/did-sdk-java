package network.platon.pid.sdk.resp.pct;

import lombok.Builder;
import lombok.Data;
import network.platon.pid.contract.dto.TransactionInfo;

@Data
public class CreatePctResp {

    private String pctId;

    private TransactionInfo transactionInfo;
}
