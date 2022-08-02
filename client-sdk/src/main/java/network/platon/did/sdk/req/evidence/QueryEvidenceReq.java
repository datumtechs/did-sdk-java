package network.platon.did.sdk.req.evidence;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class QueryEvidenceReq extends BaseReq{

	@CustomSize(min = ReqAnnoationArgs.EVIDENCE_ID_SIZE_MIN,
			max = ReqAnnoationArgs.EVIDENCE_ID_SIZE_MAX)
	private String evidenceId;
}
