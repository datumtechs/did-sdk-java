package network.platon.pid.sdk.resp.evidence;

import lombok.Data;
import network.platon.pid.contract.dto.TransactionInfo;

@Data
public class CreateEvidenceResp {

	private String evidenceId;

	private TransactionInfo transactionInfo;
	
}
