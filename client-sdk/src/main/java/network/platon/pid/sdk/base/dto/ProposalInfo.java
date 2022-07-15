package network.platon.pid.sdk.base.dto;

import lombok.Data;
import network.platon.pid.sdk.annoation.CustomNotBlank;
import network.platon.pid.sdk.annoation.CustomPattern;
import network.platon.pid.sdk.annoation.CustomSize;
import network.platon.pid.sdk.constant.PidConst;
import network.platon.pid.sdk.constant.ReqAnnoationArgs;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

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
