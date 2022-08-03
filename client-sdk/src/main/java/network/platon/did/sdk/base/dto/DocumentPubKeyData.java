package network.platon.did.sdk.base.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-12 13:56
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DocumentPubKeyData extends DidPublicKey{
    private String status;
}