package network.platon.pid.sdk.base.dto;

import lombok.Data;

/**
 * PId document publickey dto
 * @Auther: Rongjin Zhang
 * @Date: 2020年6月3日
 * @Description:
 */
@Data
public class PidPublicKey {
	
	private String id;

	private String type;
	
	private String publicKeyHex;
	
}