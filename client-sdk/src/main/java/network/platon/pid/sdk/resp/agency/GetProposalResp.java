package network.platon.pid.sdk.resp.agency;

import lombok.Data;
import network.platon.pid.sdk.base.dto.ProposalInfo;

@Data
public class GetProposalResp {
    private ProposalInfo proposal;
}
