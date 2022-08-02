package network.platon.did.sdk.base.dto;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: The base data structure of PlatON DID Document info
 * @Author: Gavin
 * @Date: 2020-06-03 17:10
 */
@Data
public class Document {

    /**
     * Required: The @context.
     */
    @JSONField(name="@context")
    private String context;

    /**
     * Required: The PlatON DID.
     */
    private String id;

    /**
     * Required: The create date.
     * This datetime value MUST be normalized to UTC 00:00, as indicated by the trailing "Z".
     * e.g. "2020-06-03T17:10:00Z"
     */
    private String created;

    /**
     * Required: The update date.
     * This datetime value MUST be normalized to UTC 00:00, as indicated by the trailing "Z".
     * e.g. "2020-06-03T17:10:00Z"
     */
    private String updated;

    /**
     * Required: The status.
     */
    private String status;
    
    /**
     * Required: The publicKey list of DID Document
     */
    private List<DidPublicKey> publicKey = new ArrayList<>();

    /**
     * Required: The authentication list of DID Document
     */
    private List<String> authentication = new ArrayList<>();

    /**
     * Required: The service list of DID Document
     */
    private List<DidService> service = new ArrayList<>();
}
