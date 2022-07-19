package network.platon.pid.sdk.req.pid;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.sdk.annoation.*;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;
import network.platon.pid.sdk.req.BaseReq;

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
        @CustomPattern(value = PidConst.PLATONE_PRIVATE_KEY_PATTERN)
        private String privateKey;

        /**
         * Required: The type.
         */
        @CustomNotNull
        @CustomIgnore
        private PidConst.PublicKeyType type;

        int index;

        /**
         * Required: The public key.
         */
        @CustomNotBlank
        @CustomSize(min = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MIN,
                max = ReqAnnoationArgs.PUBLIC_KEY_SIZE_MAX)
        @CustomPattern(value = PidConst.PLATONE_PUBLICK_KEY_PATTERN)
        private String publicKey;

        /**
         * nothing to do.
         * @param type the public key type
         */
        public void setType(PidConst.PublicKeyType type) {
            this.type = type;
        }

}
