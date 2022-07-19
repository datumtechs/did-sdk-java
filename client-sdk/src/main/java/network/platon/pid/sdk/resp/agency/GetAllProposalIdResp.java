package network.platon.pid.sdk.resp.agency;

import lombok.Data;

import java.util.List;

@Data
public class GetAllProposalIdResp {

    private List<String> proposalIds;
}
