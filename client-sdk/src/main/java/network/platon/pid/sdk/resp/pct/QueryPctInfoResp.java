package network.platon.pid.sdk.resp.pct;

import lombok.AllArgsConstructor;
import lombok.Data;
import network.platon.pid.sdk.base.dto.PctData;

@Data
@AllArgsConstructor(staticName = "of")
public class QueryPctInfoResp {

    private PctData pctInfo;
}
