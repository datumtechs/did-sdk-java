package network.platon.did.sdk.resp.evidence;

import lombok.Data;
import network.platon.did.contract.dto.TransactionInfo;

@Data
public class RevokeEvidenceResp {
    private boolean status;
    private TransactionInfo transactionInfo;
}
