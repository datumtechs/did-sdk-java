package network.platon.pid.sdk.req.evidence;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class QueryEvidenceReq extends BaseReq{

	@CustomSize(min = ReqAnnoationArgs.EVIDENCE_ID_SIZE_MIN,
			max = ReqAnnoationArgs.EVIDENCE_ID_SIZE_MAX)
	private String evidenceId;
}
