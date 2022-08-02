package network.platon.did.sdk.client;

import network.platon.did.sdk.req.presentation.CreatePresetationReq;
import network.platon.did.sdk.req.presentation.VerifyPresetationReq;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.presentation.CreatePresetationResp;
import network.platon.did.sdk.service.PresentationService;

public class PresentationClient extends BusinessClient implements PresentationService {

	@Override
	public BaseResp<CreatePresetationResp> createPresentation(CreatePresetationReq req) {
		return getPresentationService().createPresentation(req);
	}

	@Override
	public BaseResp<String> verifyPresentation(VerifyPresetationReq req) {
		return getPresentationService().verifyPresentation(req);
	}

}
