package network.platon.pid.sdk.resp.pid;

import lombok.Data;
import network.platon.pid.contract.dto.TransactionInfo;


@Data
public class ChangeDocumentStatusResp {
    private boolean status;
    private TransactionInfo transactionInfo;
}
