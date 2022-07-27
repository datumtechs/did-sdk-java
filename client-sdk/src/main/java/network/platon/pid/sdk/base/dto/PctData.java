package network.platon.pid.sdk.base.dto;

import lombok.Data;

@Data
public class PctData {

    private String pid;

    private String issue;

    private String pctJson;

    private byte[] extra;
}
