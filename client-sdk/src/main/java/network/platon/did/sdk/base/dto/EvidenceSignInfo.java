package network.platon.did.sdk.base.dto;

import lombok.Data;
import network.platon.did.sdk.annoation.CustomNotBlank;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.constant.ReqAnnoationArgs;

@Data
public class EvidenceSignInfo {

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.DID_SIZE_MIN, max = ReqAnnoationArgs.DID_SIZE_MAX)
	private String signer;
	
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.COMMON_DATA_SIZE)
	private String signature;
	
	private String timestamp;
}
