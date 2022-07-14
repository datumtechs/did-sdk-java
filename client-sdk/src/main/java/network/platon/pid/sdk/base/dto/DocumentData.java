package network.platon.pid.sdk.base.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-11 14:54
 */
@Data
public class DocumentData {


    /**
     * Required: The PlatON DID.
     */
    private String id;

    /**
     * Required: The create date.
     */
    private String created;

    /**
     * Required: The update date.
     */
    private String updated;

    /**
     * Required: The status.
     */
    private String status;

    /**
     * Required: The publicKey list of DID Document
     */
    private List<DocumentPubKeyData> publicKey = new ArrayList<>();

    /**
     * Required: The authentication list of DID Document on PIDContract
     */
    private List<DocumentAuthData> authentication = new ArrayList<>();

    /**
     * Required: The service list of DID Document
     */
    private List<DocumentServiceData> service = new ArrayList<>();
}
