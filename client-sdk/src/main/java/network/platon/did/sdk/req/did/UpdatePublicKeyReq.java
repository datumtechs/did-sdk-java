package network.platon.did.sdk.req.did;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.sdk.annoation.*;
import network.platon.did.sdk.constant.DidConst;
import network.platon.did.sdk.constant.ReqAnnoationArgs;
import network.platon.did.sdk.req.BaseReq;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-14 00:18
 */
@Data
@Builder
@EqualsAndHashCode(callSuper=false)
public class UpdatePublicKeyReq extends BaseReq {

        /**
         * Required: The PlatON DID private key.
         */
        @CustomNotBlank
        @CustomSize(min = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MIN,
                max = ReqAnnoationArgs.PRIVATE_KEY_SIZE_MAX)
        @CustomPattern(value = DidConst.PLATONE_PRIVATE_KEY_PATTERN)
        private String privateKey;

        /**
         * Required: The type.
         */
        @CustomNotNull
        @CustomIgnore
        private DidConst.PublicKeyType type;

        int index;

        /**
         * Required: The public key.
         */
        @CustomNotBlank
        @CustomSize(min = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MIN,
                max = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MAX)
        @CustomPattern(value = DidConst.PLATONE_PUBLICK_KEY_PATTERN)
        private String publicKey;

        /**
         * nothing to do.
         * @param type the public key type
         */
        public void setType(DidConst.PublicKeyType type) {
            this.type = type;
        }

}
