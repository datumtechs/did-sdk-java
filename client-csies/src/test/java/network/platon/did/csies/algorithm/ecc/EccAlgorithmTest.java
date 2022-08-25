package network.platon.did.csies.algorithm.ecc;

import com.platon.bech32.Bech32;
import com.platon.crypto.*;
import com.platon.parameters.NetworkParameters;
import com.platon.utils.Numeric;
import network.platon.did.csies.algorithm.AlgorithmHandler;
import network.platon.did.csies.utils.ConverDataUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

public class EccAlgorithmTest {

    @Test
    public void test_publicKeyFromPrivate() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        BigInteger publicKey = AlgorithmHandler.publicKeyFromPrivate(ecKeyPair.getPrivateKey());
        Assert.assertTrue(publicKey.compareTo(ecKeyPair.getPublicKey()) == 0);
    }

    @Test
    public void test_signMessage() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, DecoderException {
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        String message = "123";

        Sign.SignatureData signatureData = Sign.signMessage(message.getBytes(StandardCharsets.UTF_8), ecKeyPair);
        BigInteger signPublicKey = Sign.signedMessageToKey(message.getBytes(StandardCharsets.UTF_8), signatureData);
        System.out.println( Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey()));
        System.out.println( Numeric.toHexStringWithPrefix(signPublicKey));
        Assert.assertTrue(signPublicKey.compareTo(ecKeyPair.getPublicKey()) == 0);

        String sign = AlgorithmHandler.signMessageStr("123", ecKeyPair.getPrivateKey().toString(16));
        Assert.assertTrue(AlgorithmHandler.verifySignature("123", sign , ecKeyPair.getPublicKey()));

        String hash = "ebd5186e34a8a67b541cf27b17106bae3c77d11154d6e72c2a2f6f243bc04533";
        byte[] digest = Hex.decodeHex(hash.toCharArray());
        signatureData = Sign.signMessage(digest, ecKeyPair);
        signPublicKey = Sign.signedMessageToKey(digest, signatureData);
        System.out.println( Numeric.toHexStringWithPrefix(signPublicKey));

        Assert.assertTrue(signPublicKey.compareTo(ecKeyPair.getPublicKey()) == 0);

        String privateKey = "68efa6466edaed4918f0b6c3b1b9667d37cad591482d672e8abcb4c5d1720f89";
        ECKeyPair keyPair = AlgorithmHandler.createEcKeyPair(privateKey);

        hash = "cfe65d21f7089d98fa13f66a1b91c3dbbae7af97575ed139031a78ec4d8378da";
        digest = Hex.decodeHex(hash.toCharArray());
        signatureData = Sign.signMessage(digest, keyPair, false);

        System.out.println(EccAlgorithm.signatureToString(signatureData));

        message = "{\"@context\":\"http://datumtech.com/did/v1\",\"claimData\":\"0xf3da2ccc2b7c939d097a5e6f2fd97e21d124042101c79326438661ae266a0824\",\"claimMeta\":{\"pctId\":\"1000\"},\"expirationDate\":\"2122-08-22T07:56:47.061\",\"holder\":\"did:pid:lat1cq9svdd8vc83u74relncn6cyxywr5mjqccqlea\",\"id\":\"ec1f34e6-8980-41b4-9240-bb23d3c6dc5a\",\"issuanceDate\":\"2022-08-25T03:59:29.869\",\"issuer\":\"did:pid:lat1d7zjh2vx8xsqrgc4qe0v4usxn368naxvlpu70r\",\"type\":[\"VerifiableCredential\"],\"version\":\"1.0.0\"}";
        sign = AlgorithmHandler.signMessageStr(message, keyPair.getPrivateKey().toString(16));
        System.out.println(sign);
        System.out.println( Numeric.toHexStringWithPrefix(keyPair.getPublicKey()));
        System.out.println( keyPair.getPublicKey());
        Assert.assertTrue(AlgorithmHandler.verifySignature(message, sign , keyPair.getPublicKey()));

    }

    @Test
    public void test_go_java_private() throws DecoderException, SignatureException {

        String hash = "ebd5186e34a8a67b541cf27b17106bae3c77d11154d6e72c2a2f6f243bc04533";
        byte[] digest = Hex.decodeHex(hash.toCharArray());
        System.out.println(Hex.encodeHex(digest));

        String privateKey = "3953b6580917ad3586b4712b326c95b068861a6ae47332f4062b42c9b023a5be";
        ECKeyPair keyPair = AlgorithmHandler.createEcKeyPair(privateKey);
        System.out.println(keyPair.getPrivateKey().toString(16));

        String signPublicKeyStr = Numeric.toHexStringWithPrefix(keyPair.getPublicKey());
        System.out.println(signPublicKeyStr);

        // Sign.SignatureData signatureData =  Sign.signMessage(digest, keyPair);
        Sign.SignatureData signatureData =  Sign.signMessage(digest, keyPair, false);
        System.out.println(Hex.encodeHex(signatureData.getR()));
        System.out.println(Hex.encodeHex(signatureData.getS()));
        System.out.println(Hex.encodeHex(signatureData.getV()));

        // BigInteger signPublicKey = Sign.signedMessageToKey(digest, signatureData);
        ECDSASignature sig = new ECDSASignature(
                new BigInteger(1, signatureData.getR()),
                new BigInteger(1, signatureData.getS()));

        int header = signatureData.getV()[0] & 0xFF;
        int recId = header - 27;
        BigInteger signPublicKey = Sign.recoverFromSignature(recId, sig, digest);
        signPublicKeyStr = Numeric.toHexStringWithPrefix(signPublicKey);
        System.out.println(signPublicKeyStr);

        Assert.assertTrue(signPublicKey.compareTo(keyPair.getPublicKey()) == 0);

        String goSignDataStr = "6bbb8a0202fec66294718e7795647ce645b9b513c016e03340915f56eac609a07883b85f589476eb04ffa190b919c2aa5972f3c5077d906dd7f704abcefc1fd400";
        byte[] goSignData = Hex.decodeHex(goSignDataStr.toCharArray());
        byte[] v = new byte[1];
        byte[] r = new byte[32];
        byte[] s = new byte[32];
        System.arraycopy(goSignData, 0, r, 0, 32);
        System.arraycopy(goSignData, 32, s, 0, 32);
        System.arraycopy(goSignData, 64, v, 0, 1);
        System.out.println(Hex.encodeHex(r));
        System.out.println(Hex.encodeHex(s));
        System.out.println(Hex.encodeHex(v));

        sig = new ECDSASignature(
                new BigInteger(1, r),
                new BigInteger(1, s));

        recId = v[0] & 0xFF;
        signPublicKey = Sign.recoverFromSignature(recId, sig, digest);
        Assert.assertTrue(signPublicKey.compareTo(keyPair.getPublicKey()) == 0);

        signPublicKeyStr = Numeric.toHexStringWithPrefix(signPublicKey);
        System.out.println(signPublicKeyStr);
        String address = Keys.getAddress(signPublicKeyStr);
        address = Bech32.addressEncode(NetworkParameters.getHrp(), address);

        System.out.println(address);
    }


}
