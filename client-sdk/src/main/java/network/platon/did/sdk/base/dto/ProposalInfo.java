package network.platon.did.sdk.base.dto;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-18 10:28
 */
@Data
public class ProposalInfo {

    private BigInteger proposalType;
    private String proposalUrl;
    private String submitter;
    private String candidate;
    private String candidateServiceUrl;
    private BigInteger submitBlockNo;
    private List<String> voters;

}
