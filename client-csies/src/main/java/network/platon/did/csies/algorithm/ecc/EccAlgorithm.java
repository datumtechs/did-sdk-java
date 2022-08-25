package network.platon.did.csies.algorithm.ecc;

import com.platon.crypto.ECKeyPair;
import com.platon.crypto.Keys;
import com.platon.crypto.Sign;
import com.platon.utils.Numeric;
import lombok.extern.slf4j.Slf4j;
import network.platon.did.csies.algorithm.Algorithm;
import network.platon.did.csies.utils.ConverDataUtils;
import org.apache.commons.codec.DecoderException;
import org.bouncycastle.util.encoders.Base64;
import org.apache.commons.codec.binary.Hex;

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
        return Sign.signMessage(message.getBytes(StandardCharsets.UTF_8), keyPair);
    }
	
	/**
	 * Convert signature to public key
	 * @param message
	 * @param signatureData
	 * @return
	 * @throws SignatureException
	 */
	private BigInteger signatureToPublicKey(String message, Sign.SignatureData signatureData) throws SignatureException {
	    return Sign.signedMessageToKey(message.getBytes(StandardCharsets.UTF_8),
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
		} catch (SignatureException | DecoderException e) {
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
	
	public static String signatureToString(Sign.SignatureData signatureData) {
        byte[] serializedSignatureData = new byte[65];
        System.arraycopy(signatureData.getR(), 0, serializedSignatureData, 0, 32);
        System.arraycopy(signatureData.getS(), 0, serializedSignatureData, 32, 32);
        System.arraycopy(signatureData.getV(), 0, serializedSignatureData, 64, 1);
		int header = signatureData.getV()[0] & 0xFF;
		int recId = header - 27;
		serializedSignatureData[64] = (byte)recId;
        return new String(Hex.encodeHex(serializedSignatureData));
    }
	
	public static Sign.SignatureData stringToSignature(String signatureData) throws DecoderException {
		if(signatureData.startsWith("0x")){
			signatureData = signatureData.substring(2);
		}
		byte[] serializedSignatureData = Hex.decodeHex(signatureData.toCharArray());
		byte[] r = new byte[32];
		byte[] s = new byte[32];
		byte[] v = new byte[1];
		System.arraycopy(serializedSignatureData, 0, r, 0, 32);
        System.arraycopy(serializedSignatureData, 32, s, 0, 32);
        System.arraycopy(serializedSignatureData, 64, v, 0, 1);
		int header = v[0] & 0xFF;
		int recId = header + 27;
		v[0] = (byte)recId;
        return new Sign.SignatureData(v, r, s);
    }
	
	public String signMessageStr(String message,String privateKeyStr) {
        BigInteger privateKey = Numeric.toBigInt(privateKeyStr);
        ECKeyPair keyPair = ECKeyPair.create(privateKey);
		Sign.SignatureData signatureData = Sign.signMessage(message.getBytes(StandardCharsets.UTF_8), keyPair);
        return signatureToString(signatureData);
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
