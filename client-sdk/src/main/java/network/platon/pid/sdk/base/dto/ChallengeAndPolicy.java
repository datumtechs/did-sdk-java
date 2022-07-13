package network.platon.pid.sdk.base.dto;

import lombok.Data;

/**
 * @Description: Wrapper  for Challenge and Policy
 * @Author: Gavin
 * @Date: 2020-06-05 16:18
 */
@Data
public class ChallengeAndPolicy {

    private PresentationPolicy presentationPolicy;

    private Challenge challenge;
}
