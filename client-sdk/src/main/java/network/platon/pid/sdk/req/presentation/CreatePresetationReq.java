package network.platon.pid.sdk.req.presentation;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomNotNull;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.base.dto.Challenge;
import network.platon.pid.sdk.base.dto.Credential;
import network.platon.pid.sdk.base.dto.PidAuthentication;
import network.platon.pid.sdk.base.dto.PresentationPolicy;
import network.platon.pid.sdk.req.BaseReq;

import java.util.List;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-05 17:23
 */
@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class CreatePresetationReq extends BaseReq{

	@CustomNotNull
    @CustomSize(min = 1)
    private List<Credential> credentials;

    @CustomNotNull
    private PidAuthentication authentication;

	@CustomNotNull
    private Challenge challenge;

	@CustomNotNull
    private PresentationPolicy policy;

}
