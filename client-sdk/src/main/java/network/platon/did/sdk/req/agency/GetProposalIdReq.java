package network.platon.did.sdk.req.agency;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomMin;
import network.platon.did.sdk.annoation.CustomNotNull;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

import java.math.BigInteger;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class GetProposalIdReq extends BaseReq {

    @CustomNotNull
    @CustomMin(value = ReqAnnoationArgs.COMMON_DATA_SIZE)
    private BigInteger blockNo;
}
