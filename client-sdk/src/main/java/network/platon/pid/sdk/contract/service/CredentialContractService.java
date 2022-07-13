package network.platon.pid.sdk.contract.service;

import network.platon.pid.contract.dto.CredentialEvidence;
import network.platon.pid.sdk.resp.TransactionResp;

/**
 * 
 * @Auther: Rongjin Zhang
 * @Date: 2020年6月16日
 * @Description:
 */
public interface CredentialContractService {


    /**
     * Create credential in CredentialContract
     * @return
     */
    TransactionResp<String> createCredentialEvience(String hash, String signer, String signatureData);

    /**
     * Query credential by hash
     * @param hash
     * @return
     */
    TransactionResp<CredentialEvidence> queryCredentialEvience(String hash);
    
    TransactionResp<Boolean> isHashExit(String hash);
}
