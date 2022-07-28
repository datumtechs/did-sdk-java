package network.platon.pid.sdk.service;

import network.platon.pid.sdk.req.evidence.RevokeEvidenceReq;
import network.platon.pid.sdk.resp.evidence.QueryEvidenceResp;
import network.platon.pid.sdk.req.evidence.CreateEvidenceReq;
import network.platon.pid.sdk.req.evidence.QueryEvidenceReq;
import network.platon.pid.sdk.req.evidence.VerifyCredentialEvidenceReq;
import network.platon.pid.sdk.req.evidence.VerifyEvidenceReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.evidence.CreateEvidenceResp;
import network.platon.pid.sdk.resp.evidence.RevokeEvidenceResp;

/**
 * The evidence interface class contains methods for creating, query and verify evidence
 * @Auther: Rongjin Zhang
 * @Date: 2020��6��3��
 * @Description:
 */
public interface EvidenceService {

	/**
	 * Create evidence based on request parameters.
	 * Institutional users can use this method to generate credentials to register on the PlatON
	 * @param req
	 * @return
	 */
	BaseResp<CreateEvidenceResp> createEvidence(CreateEvidenceReq req);
	
	/**
	 * Query evidence info based on request parameters,used to verify evidence
	 * The verification agency can query the credential information on the PlatON
	 * @param req
	 * @return
	 */
	BaseResp<QueryEvidenceResp> queryEvidence(QueryEvidenceReq req);

	/**
	 * revokeCredential
	 * @param req
	 * @return
	 */
	BaseResp<RevokeEvidenceResp> revokeEvidence(RevokeEvidenceReq req);
	
	
	/**
	 * Verify evidence based on request parameters
	 * The verification agency can check whether the credential information on the PlatON is legal
	 * @param req
	 * @return
	 */
	BaseResp<String> verifyCredentialEvidence(VerifyCredentialEvidenceReq req);
	
}
