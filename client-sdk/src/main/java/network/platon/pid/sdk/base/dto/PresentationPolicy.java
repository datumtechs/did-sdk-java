package network.platon.pid.sdk.base.dto;

import lombok.Data;
import network.platon.pid.sdk.annoation.CustomNotNull;
import network.platon.pid.sdk.enums.PresentationPolicyType;

import java.util.Map;

@Data
public class PresentationPolicy {
//
//    /**
//     * Policy ID.
//     */
//    private Integer id;
//
//    /**
//     * A pid who publish this presentation policy.
//     */
//    private String publisher;

    /**
     *
     */
    private int policyType = PresentationPolicyType.POLICY_DEFAULT.getType();

    @CustomNotNull
    private Map<String, ClaimPolicy> policys;
}
