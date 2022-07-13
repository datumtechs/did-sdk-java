package network.platon.pid.sdk.resp.pid;


import lombok.Data;
import network.platon.pid.sdk.base.dto.Document;

@Data
public class QueryPidDocumentResp {

	private Document document;

	public QueryPidDocumentResp(Document document){
		this.document = document;
	}

}
