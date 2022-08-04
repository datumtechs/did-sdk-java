package network.platon.did.sdk.req.presentation;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomNotNull;
import network.platon.did.sdk.annoation.CustomPattern;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.base.dto.Challenge;
import network.platon.did.sdk.base.dto.Presentation;
import network.platon.did.sdk.base.dto.PresentationPolicy;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;


/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-05 17:23
 */
@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class VerifyPresetationReq extends BaseReq{

	@CustomNotNull
    @CustomSize(min = ReqAnnoationArgs.DID_SIZE_MIN,
            max = ReqAnnoationArgs.DID_SIZE_MAX)
    @CustomPattern(value = DidConst.PLATON_DID_PATTERN)
    private String did;

	@CustomNotNull
    private Presentation presentation;

	@CustomNotNull
    private Challenge challenge;
}
