package network.platon.pid.sdk.base.dto;

import lombok.Data;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;

@Data
public class EvidenceSignInfo {

	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.PID_SIZE_MIN, max = ReqAnnoationArgs.PID_SIZE_MAX)
	private String signer;
	
	@CustomNotBlank
	@CustomSize(min = ReqAnnoationArgs.COMMON_DATA_SIZE)
	private String signature;
	
	private Long timestamp;
}
