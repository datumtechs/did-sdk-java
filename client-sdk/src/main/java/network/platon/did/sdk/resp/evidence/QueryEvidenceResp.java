package network.platon.did.sdk.resp.evidence;

import lombok.Data;
import network.platon.did.sdk.base.dto.EvidenceSignInfo;

@Data
public class QueryEvidenceResp {

	private String credentialHash;
	
	private EvidenceSignInfo signInfo;

	private String status;
}
