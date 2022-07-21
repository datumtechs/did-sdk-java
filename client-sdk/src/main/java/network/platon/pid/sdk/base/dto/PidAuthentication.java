package network.platon.pid.sdk.base.dto;

import lombok.Data;
import network.platon.pid.sdk.annoation.CustomNotBlank;

@Data
public class PidAuthentication {
    @CustomNotBlank
    private String pid;

    @CustomNotBlank
    private String publicKeyId;

    @CustomNotBlank
    private String privateKey;

    public PidAuthentication() {
        super();
    }

    public PidAuthentication(String pid, String privateKey, String publicKeyId) {
        this.pid = pid;
        this.privateKey = privateKey;
        this.publicKeyId = publicKeyId;
    }
}
