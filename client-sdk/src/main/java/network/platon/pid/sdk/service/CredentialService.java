package network.platon.pid.sdk.service;

import network.platon.pid.sdk.req.credential.CreateCredentialReq;
import network.platon.pid.sdk.req.credential.CreateSelectCredentialReq;
import network.platon.pid.sdk.req.credential.VerifyCredentialReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.credential.CreateCredentialResp;
import network.platon.pid.sdk.resp.credential.CreateSelectCredentialResp;

/**
 * The credential interface class contains methods for creating, verifying, and removing credentials
 * Note: This part of the data is not stored on the chain, please save your credentials when using it
 * @Auther: Rongjin Zhang
 * @Date: 2020年6月3日
 * @Description:
 */
public interface CredentialService {

	/**
	 * Create credential based on request parameters.
	 * Institutional users can issue certificates according to this method
	 * @param req
	 * @return
	 */
	BaseResp<CreateCredentialResp> createCredential(CreateCredentialReq req);
	
	/**
	 * Verify credential based on request parameters
	 * Institutional users can verify the validity of the signature of the certificate by this method
	 * @param req
	 * @return
	 */
	BaseResp<String> verifyCredential(VerifyCredentialReq req);

	/**
	 * Create select credential based on request parameters.
	 * Institutional users can issue certificates according to this method
	 * @param req
	 * @return
	 */
	BaseResp<CreateSelectCredentialResp> createSelectCredential(CreateSelectCredentialReq req);
	
}
