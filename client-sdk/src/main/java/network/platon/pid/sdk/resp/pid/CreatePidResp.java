package network.platon.pid.sdk.resp.pid;

import lombok.Data;
import network.platon.pid.contract.dto.TransactionInfo;

/**
 * create pid return dto
 * @Auther: Zhangrj
 * @Date: 2020年5月29日
 * @Description:
 */
@Data
public class CreatePidResp {

	private String privateKey;
	
	private String publicKey;
	
	private String pid;

	private TransactionInfo transactionInfo;
	
}
