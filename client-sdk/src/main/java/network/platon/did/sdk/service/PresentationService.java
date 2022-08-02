package network.platon.did.sdk.service;

import network.platon.did.sdk.req.presentation.CreatePresetationReq;
import network.platon.did.sdk.req.presentation.VerifyPresetationReq;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.presentation.CreatePresetationResp;

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
