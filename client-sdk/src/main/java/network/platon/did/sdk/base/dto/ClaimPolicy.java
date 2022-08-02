package network.platon.did.sdk.base.dto;

import lombok.Data;
import network.platon.did.sdk.annoation.CustomNotBlank;

/**
 * @Description: A kind of Calim's policy constraints between Credential holders and validators
 * @Author: Gavin
 * @Date: 2020-06-05 16:26
 */
@Data
public class ClaimPolicy {

    /**
     * Indicates that the current Claim needs to selectively disclose the field information.
     * It must be in the form of a JSON string.
     */
	@CustomNotBlank
    private String disclosedFieldsJson;
}
