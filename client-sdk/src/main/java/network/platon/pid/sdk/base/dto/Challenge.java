package network.platon.pid.sdk.base.dto;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.csies.utils.ConverDataUtils;
import network.platon.pid.common.enums.DataTypeCastEnum;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.exception.DataTypeCastException;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import org.bouncycastle.util.encoders.Base64;

@Data
@Slf4j
public class Challenge {


//    /**
//     * Specify who you want to challenge.
//     */
//    @CustomNotBlank
//    private String  pid;

    /**
     * A random number issued to the challenged user.
     * The challenged user must use the random number to sign Credential.
     */
    @CustomNotBlank
    private String nonce;


    public static Challenge create(String pid, String seed) {

        SecureRandom random = new SecureRandom();
        String randomSeed = seed + ConverDataUtils.generalUUID();
        random.setSeed(randomSeed.getBytes());
        byte[] bytes = new byte[15];
        random.nextBytes(bytes);
        String nonce = Base64.toBase64String(bytes);

        Challenge challenge = new Challenge();
        challenge.setNonce(nonce);
//        challenge.setPid(pid);
        return challenge;
    }

    public String toJson() {
        return ConverDataUtils.serialize(this);
    }


    public static Challenge fromJson(String challengeJson) {
        if (StringUtils.isBlank(challengeJson)) {
            log.error("failed to create Challenge with JSON string. the Challenge JSON String is null");
            throw new DataTypeCastException(DataTypeCastEnum.DATATYPECAST_STR2JSON_FAILED.getCode(), "the Challenge JSON String is null.");
        }
        return ConverDataUtils.deserialize(challengeJson, Challenge.class);
    }


    public String toRawData() {
        return this.nonce;
    }

    private Challenge() {
    }
}
