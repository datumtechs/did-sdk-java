package network.platon.pid.csies.algorithm;

import com.platon.crypto.ECKeyPair;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @program: PlatON-DID
 * @description:
 * @author: Rongjin Zhang
 * @create: 2020-09-01 15:04
 */
public interface Algorithm {

    Boolean verifySignature( String message,String signatureData, BigInteger publicKey);

    BigInteger publicKeyFromPrivate(BigInteger privateKey);

    String signMessageStr(String message,String privateKeyStr);

    ECKeyPair createEcKeyPair(String privateKey);

    ECKeyPair createEcKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
}
