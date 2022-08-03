package network.platon.did.sdk.req.pct;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomNotBlank;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
public class QueryPctInfoReq extends BaseReq {
    @CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PCT_ID_SIZE_MIN)
    private String pctId;
}
