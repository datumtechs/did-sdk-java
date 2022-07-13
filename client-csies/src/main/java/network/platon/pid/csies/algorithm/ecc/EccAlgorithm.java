package network.platon.pid.csies.algorithm.ecc;

import com.platon.crypto.ECKeyPair;
import com.platon.crypto.Keys;
import com.platon.crypto.Sign;
import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.pid.csies.algorithm.Algorithm;
import network.platon.pid.csies.utils.ConverDataUtils;
import org.bouncycastle.util.encoders.Base64;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;

/**
 * Related implementation of ecc algorithm
 * @Auther: Rongjin Zhang
 * @Date: 2020年6月5日
 * @Description:
 */
@Slf4j
public class EccAlgorithm implements Algorithm  {

	/**
	 * Data signing according to ECKeyPair
	 * @param message
	 * @param keyPair
	 * @return
	 */
	public static Sign.SignatureData signMessage(String message, ECKeyPair keyPair) {
        return Sign.signMessage(ConverDataUtils.sha3(message.getBytes(StandardCharsets.UTF_8)), keyPair);
    }
	
	/**
	 * Convert signature to public key
	 * @param message
	 * @param signatureData
	 * @return
	 * @throws SignatureException
	 */
	private BigInteger signatureToPublicKey(String message, Sign.SignatureData signatureData) throws SignatureException {
	    return Sign.signedMessageToKey(ConverDataUtils.sha3(message.getBytes(StandardCharsets.UTF_8)),
	            signatureData);
	}
	
	/**
	 * Verify that the signature data and public key match
	 * @param message
	 * @param signatureData
	 * @param publicKey
	 * @return
	 */
	public Boolean verifySignature( String message,String signatureData, BigInteger publicKey) {
	     
		BigInteger signPublicKey;
		try {
			signPublicKey = signatureToPublicKey(message, stringToSignature(signatureData));
			return publicKey.compareTo(signPublicKey) == 0;
		} catch (SignatureException e) {
			log.error("verifySignature error", e);
		}
	    return false;
	}
	
	/**
	 * Convert privateKey to publicKey
	 * @param privateKey
	 * @return
	 */
	public BigInteger publicKeyFromPrivate(BigInteger privateKey) {
        return Sign.publicKeyFromPrivate(privateKey);
    }
	
	private static String signatureToString(Sign.SignatureData signatureData) {
        byte[] serializedSignatureData = new byte[65];
        System.arraycopy(signatureData.getV(), 0, serializedSignatureData, 0, 1);
        System.arraycopy(signatureData.getR(), 0, serializedSignatureData, 1, 32);
        System.arraycopy(signatureData.getS(), 0, serializedSignatureData, 33, 32);
        return Base64.toBase64String(serializedSignatureData);
    }
	
	private static Sign.SignatureData stringToSignature(String signatureData) {
		byte[] serializedSignatureData = Base64.decode(signatureData);
		byte[] v = new byte[1];
		byte[] r = new byte[32];
		byte[] s = new byte[32];
		System.arraycopy(serializedSignatureData, 0, v, 0, 1);
        System.arraycopy(serializedSignatureData, 1, r, 0, 32);
        System.arraycopy(serializedSignatureData, 33, s, 0, 32);
        return new Sign.SignatureData(v, r, s);
    }
	
	public String signMessageStr(String message,String privateKeyStr) {
        BigInteger privateKey = Numeric.toBigInt(privateKeyStr);
        ECKeyPair keyPair = ECKeyPair.create(privateKey);
        return signatureToString(Sign.signMessage(ConverDataUtils.sha3(message.getBytes(StandardCharsets.UTF_8)), keyPair));
	}

	@Override
	public ECKeyPair createEcKeyPair(String privateKey) {
		return ECKeyPair.create(Numeric.hexStringToByteArray(privateKey));
	}

	@Override
	public ECKeyPair createEcKeyPair() {
		try{
			return Keys.createEcKeyPair();
		} catch (Exception e){
			log.error("error" , e);
			return  null;
		}
	}
}
