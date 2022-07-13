package network.platon.pid.sdk.resp.pct;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class QueryPctJsonResp {

	private String pid;
	
	private String pctJson;
	
}
