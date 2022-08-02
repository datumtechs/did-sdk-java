package network.platon.did.sdk.resp.did;

import lombok.Data;
import network.platon.did.contract.dto.TransactionInfo;

/**
 * create did return dto
 * @Auther: Zhangrj
 * @Date: 2020年5月29日
 * @Description:
 */
@Data
public class CreateDidResp {

	private String privateKey;

	private String did;

	private String type;

	private String publicKey;

	private TransactionInfo transactionInfo;
	
}
