package network.platon.pid.sdk.resp.evidence;

import lombok.Data;
import network.platon.pid.sdk.base.dto.EvidenceSignInfo;

@Data
public class QueryEvidenceResp {

	private String credentialHash;
	
	private EvidenceSignInfo signInfo;

	private String status;
}
