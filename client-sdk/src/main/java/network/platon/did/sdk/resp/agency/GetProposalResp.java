package network.platon.did.sdk.resp.agency;

import lombok.Data;
import network.platon.did.sdk.base.dto.ProposalInfo;

@Data
public class GetProposalResp {
    private ProposalInfo proposal;
}
