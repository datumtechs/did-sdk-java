package network.platon.did.sdk.client;

import network.platon.did.sdk.req.evidence.*;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.evidence.CreateEvidenceResp;
import network.platon.did.sdk.resp.evidence.QueryEvidenceResp;
import network.platon.did.sdk.resp.evidence.RevokeEvidenceResp;
import network.platon.did.sdk.service.EvidenceService;

public class EvidenceClient extends BusinessClient implements EvidenceService  {

	@Override
	public BaseResp<CreateEvidenceResp> createEvidence(CreateEvidenceReq req) {
		return getEvidenceService().createEvidence(req);
	}

	@Override
	public BaseResp<QueryEvidenceResp> queryEvidence(QueryEvidenceReq req) {
		return getEvidenceService().queryEvidence(req);
	}

	@Override
	public BaseResp<String> verifyEvidence(VerifyEvidenceReq req) {
		return getEvidenceService().verifyEvidence(req);
	}

	@Override
	public BaseResp<RevokeEvidenceResp> revokeEvidence(RevokeEvidenceReq req) {
		return getEvidenceService().revokeEvidence(req);
	}

	@Override
	public BaseResp<String> verifyCredentialEvidence(VerifyCredentialEvidenceReq req) {
		return getEvidenceService().verifyCredentialEvidence(req);
	}


}
