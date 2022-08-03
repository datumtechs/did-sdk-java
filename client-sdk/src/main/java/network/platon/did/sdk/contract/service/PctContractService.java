package network.platon.did.sdk.contract.service;

import network.platon.did.sdk.base.dto.PctData;
import network.platon.did.sdk.resp.BaseResp;
import network.platon.did.sdk.resp.TransactionResp;

import java.math.BigInteger;


/**
 * @author yzw
 */
public interface PctContractService {


	/**
	 * register a new pctSchema by issuer
	 * @param issuer   It is string format of did
	 * @param pctJson
	 * @return
	 */
	TransactionResp<BigInteger> registerPct(String pctJson, byte[] extra) ;

	/**
	 * query a pctSchema by pctId
	 * @param pctId
	 * @return
	 */
	BaseResp<PctData> queryPctById(String pctId);
}
