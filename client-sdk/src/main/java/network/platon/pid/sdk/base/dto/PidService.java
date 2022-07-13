package network.platon.pid.sdk.base.dto;

import lombok.Data;

/**
 * Pid document service dto
 * @Auther: Rongjin Zhang
 * @Date: 2020年6月3日
 * @Description:
 */
@Data
public class PidService {

	private String id;
	
	private String type;
	
	private String serviceEndpoint;
}
