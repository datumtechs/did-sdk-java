package network.platon.pid.sdk.client;

import network.platon.pid.sdk.req.credential.CreateCredentialReq;
import network.platon.pid.sdk.req.credential.CreateSelectCredentialReq;
import network.platon.pid.sdk.req.credential.VerifyCredentialReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.credential.CreateCredentialResp;
import network.platon.pid.sdk.resp.credential.CreateSelectCredentialResp;
import network.platon.pid.sdk.service.CredentialService;

public class CredentialClient extends BusinessClient implements CredentialService  {

	@Override
	public BaseResp<CreateCredentialResp> createCredential(CreateCredentialReq req) {
		return getCredentialService().createCredential(req);
	}

	@Override
	public BaseResp<String> verifyCredential(VerifyCredentialReq req) {
		return getCredentialService().verifyCredential(req);
	}

	@Override
	public BaseResp<CreateSelectCredentialResp> createSelectCredential(CreateSelectCredentialReq req) {
		return getCredentialService().createSelectCredential(req);
	}

}
