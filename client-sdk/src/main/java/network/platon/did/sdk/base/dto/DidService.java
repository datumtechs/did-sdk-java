package network.platon.did.sdk.base.dto;

import lombok.Data;

/**
 * Did document service dto
 * @Auther: Rongjin Zhang
 * @Date: 2020年6月3日
 * @Description:
 */
@Data
public class DidService {

	private String id;
	
	private String type;
	
	private String serviceEndpoint;
}
