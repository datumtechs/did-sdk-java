package network.platon.pid.sdk.client;

import network.platon.pid.sdk.req.presentation.CreatePresetationReq;
import network.platon.pid.sdk.req.presentation.VerifyPresetationReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.presentation.CreatePresetationResp;
import network.platon.pid.sdk.service.PresentationService;

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
