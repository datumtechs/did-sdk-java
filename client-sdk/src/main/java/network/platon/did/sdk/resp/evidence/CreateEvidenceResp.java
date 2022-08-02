package network.platon.did.sdk.resp.evidence;

import lombok.Data;
import network.platon.did.contract.dto.TransactionInfo;

@Data
public class CreateEvidenceResp {

	private String evidenceId;

	private TransactionInfo transactionInfo;
	
}
