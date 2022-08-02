package network.platon.did.sdk.resp.pct;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class QueryPctJsonResp {

	private String did;
	
	private String pctJson;
	
}
