package network.platon.did.sdk.base.dto;

import lombok.Data;

@Data
public class PctData {

    private String pctId;

    private String issue;

    private String pctJson;

    private byte[] extra;
}
