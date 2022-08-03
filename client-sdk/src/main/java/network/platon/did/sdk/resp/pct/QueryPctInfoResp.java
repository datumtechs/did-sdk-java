package network.platon.did.sdk.resp.pct;

import lombok.AllArgsConstructor;
import lombok.Data;
import network.platon.did.sdk.base.dto.PctData;

@Data
@AllArgsConstructor(staticName = "of")
public class QueryPctInfoResp {

    private PctData pctInfo;

}
