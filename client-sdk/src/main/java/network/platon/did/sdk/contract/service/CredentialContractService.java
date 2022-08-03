package network.platon.did.sdk.contract.service;

import network.platon.did.contract.dto.CredentialEvidence;
import network.platon.did.sdk.resp.TransactionResp;

import java.math.BigInteger;

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
    TransactionResp<String> createCredentialEvience(String hash, String signerPublicKey, String signatureData, String updateTime);

    /**
     * Query credential by hash
     * @param hash
     * @return
     */
    TransactionResp<CredentialEvidence> queryCredentialEvience(String hash);
    
    TransactionResp<Boolean> isHashExit(String hash);

    TransactionResp<BigInteger> getStatus(String hash);

    TransactionResp<Boolean> changeStatus(String hash,BigInteger status);
}
