package network.platon.pid.sdk.req.agency;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.CustomMin;
import network.platon.pid.sdk.annoation.CustomNotNull;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

import java.math.BigInteger;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class GetProposalReq extends BaseReq {
    @CustomNotNull
    @CustomMin(value = ReqAnnoationArgs.COMMON_DATA_SIZE)
    private BigInteger proposalId;
}

