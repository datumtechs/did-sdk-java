package network.platon.pid.sdk.resp.agency;

import lombok.Data;

import java.util.List;

@Data
public class GetProposalIdResp {
    private List<String> proposalIds;
}
