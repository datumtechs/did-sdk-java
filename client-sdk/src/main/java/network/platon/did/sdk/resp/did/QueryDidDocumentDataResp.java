package network.platon.did.sdk.resp.did;

import lombok.Data;
import network.platon.did.sdk.base.dto.DocumentData;
@Data
public class QueryDidDocumentDataResp {

    private DocumentData documentData;

    public QueryDidDocumentDataResp(DocumentData data){
        this.documentData = data;
    }

}
