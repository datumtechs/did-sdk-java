package network.platon.did.sdk.req.did;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.CustomIgnore;
import network.platon.did.sdk.annoation.CustomNotBlank;
import network.platon.did.sdk.annoation.CustomPattern;
import network.platon.did.sdk.annoation.CustomSize;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class CreateDidReq extends BaseReq{

    /**
     * Required: The private key of the signed transaction is also used to generate the corresponding public key, and the DID is generated from the public key.
     */
    @CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
            max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
    @CustomPattern(value = DidConst.PLATONE_PRIVATE_KEY_PATTERN)
    private String privateKey;

    /**
     * Required: The public key in DID Document.
     */
    @CustomNotBlank
    @CustomSize(min = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MIN,
            max = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MAX)
    @CustomPattern(value = DidConst.PLATONE_PUBLICK_KEY_PATTERN)
    private String publicKey;

    /**
     * Required: The type.  (default: Secp256k1)
     */
    @Builder.Default
    @CustomIgnore
    private String type = DidConst.PublicKeyType.SECP256K1.getTypeName();
}
