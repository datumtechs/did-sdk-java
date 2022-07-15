package network.platon.pid.sdk.service;

import network.platon.pid.sdk.req.presentation.CreatePresetationReq;
import network.platon.pid.sdk.req.presentation.VerifyPresetationReq;
import network.platon.pid.sdk.resp.BaseResp;
import network.platon.pid.sdk.resp.presentation.CreatePresetationResp;

public interface PresentationService {
    /**
     * Create select presentation based on request parameters.
     *
     * @param req
     * @return
     */
    BaseResp<CreatePresetationResp> createPresentation(CreatePresetationReq req);


    /**
     * Verify  select presentation based on request parameters.
     * @param req
     * @return
     */
    BaseResp<String> verifyPresentation(VerifyPresetationReq req);
}
