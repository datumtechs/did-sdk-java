package network.platon.did.sdk.resp.did;


import lombok.Data;
import network.platon.did.sdk.base.dto.Document;

@Data
public class QueryDidDocumentResp {

	private Document document;

	public QueryDidDocumentResp(Document document){
		this.document = document;
	}

}
