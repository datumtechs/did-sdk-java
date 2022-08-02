package network.platon.did.sdk.base.dto;

import lombok.Data;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-11 14:56
 */
@Data
public class DocumentAuthData {

    private String publicKeyHex;

    private String controller;

    private String status;
}
