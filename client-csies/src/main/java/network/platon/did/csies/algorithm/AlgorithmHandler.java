package network.platon.did.csies.algorithm;

import com.platon.crypto.ECKeyPair;
import network.platon.did.csies.algorithm.ecc.EccAlgorithm;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @program: PlatON-DID
 * @description:
 * @author: Rongjin Zhang
 * @create: 2020-09-01 15:10
 */
public class AlgorithmHandler {

    private static Algorithm algorithm ;

    private static void initAlgorithm(){
        algorithm = new EccAlgorithm();
    }

    public static Boolean verifySignature(String message, String signatureData, BigInteger publicKey) {
        initAlgorithm();
        return  algorithm.verifySignature(message,signatureData,publicKey);
    }

    public static BigInteger publicKeyFromPrivate(BigInteger privateKey) {
        initAlgorithm();
        return  algorithm.publicKeyFromPrivate( privateKey);
    }

    public static String signMessageStr(String message, String privateKeyStr) {
        initAlgorithm();
        return  algorithm.signMessageStr(message, privateKeyStr);
    }

    public static ECKeyPair createEcKeyPair(String privateKeyStr) {
        initAlgorithm();
        return  algorithm.createEcKeyPair( privateKeyStr);
    }

    public static ECKeyPair createEckeypair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        initAlgorithm();
        return  algorithm.createEcKeyPair();
    }
}
