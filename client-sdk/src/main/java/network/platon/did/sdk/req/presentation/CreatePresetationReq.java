package network.platon.did.sdk.req.presentation;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomIgnore;
import network.platon.did.sdk.annoation.CustomNotNull;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.base.dto.Challenge;
import network.platon.did.sdk.base.dto.Credential;
import network.platon.did.sdk.base.dto.DidAuthentication;
import network.platon.did.sdk.base.dto.PresentationPolicy;
import network.platon.did.sdk.req.BaseReq;

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
    private DidAuthentication authentication;

	@CustomNotNull
    private Challenge challenge;

	@CustomNotNull
    private PresentationPolicy policy;

    @Builder.Default
    @CustomIgnore
    private String context = "https://datumtech.com/presentation/v1";
}
