package network.platon.pid.sdk.resp.pid;

import lombok.Data;
import network.platon.pid.sdk.base.dto.DocumentData;
@Data
public class QueryPidDocumentDataResp {

    private DocumentData documentData;

    public QueryPidDocumentDataResp(DocumentData data){
        this.documentData = data;
    }

}
