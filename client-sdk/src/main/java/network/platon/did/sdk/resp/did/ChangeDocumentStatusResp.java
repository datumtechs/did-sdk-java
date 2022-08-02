package network.platon.did.sdk.resp.did;

import lombok.Data;
import network.platon.did.contract.dto.TransactionInfo;


@Data
public class ChangeDocumentStatusResp {
    private boolean status;
    private TransactionInfo transactionInfo;
}
