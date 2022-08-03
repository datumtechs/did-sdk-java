package network.platon.did.sdk.base.dto;

import lombok.Data;

/**
 * DId document publickey dto
 * @Auther: Rongjin Zhang
 * @Date: 2020年6月3日
 * @Description:
 */
@Data
public class DidPublicKey {
	
	private String id;

	private String type;
	
	private String publicKeyHex;
	
}