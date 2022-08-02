package network.platon.did.sdk.base.dto;

import lombok.Data;
import network.platon.did.sdk.annoation.CustomNotBlank;

@Data
public class DidAuthentication {
    @CustomNotBlank
    private String did;

    @CustomNotBlank
    private String publicKeyId;

    @CustomNotBlank
    private String privateKey;

    public DidAuthentication() {
        super();
    }

    public DidAuthentication(String did, String privateKey, String publicKeyId) {
        this.did = did;
        this.privateKey = privateKey;
        this.publicKeyId = publicKeyId;
    }
}
