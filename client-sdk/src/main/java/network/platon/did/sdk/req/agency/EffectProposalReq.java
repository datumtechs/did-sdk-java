package network.platon.did.sdk.req.agency;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.*;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

import java.math.BigInteger;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class EffectProposalReq extends BaseReq {
    @CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
            max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
    @CustomPattern(value = DidConst.PLATON_PRIVATE_KEY_PATTERN)
    private String privateKey;

    @CustomNotNull
    @CustomMin(value = ReqAnnoationArgs.COMMON_DATA_SIZE)
    private BigInteger proposalId;
}
