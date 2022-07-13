package network.platon.pid.csies.algorithm.ecc;

import com.platon.crypto.ECKeyPair;
import com.platon.crypto.Keys;
import com.platon.crypto.Sign;
import network.platon.pid.common.config.PidConfig;
import network.platon.pid.csies.algorithm.AlgorithmHandler;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class EccAlgorithmTest {

    @Test
    public void test_publicKeyFromPrivate() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {

        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        BigInteger publicKey = AlgorithmHandler.publicKeyFromPrivate(ecKeyPair.getPrivateKey());
        Assert.assertTrue(publicKey.compareTo(ecKeyPair.getPublicKey()) == 0);
    }

    @Test
    public void test_signMessage() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        Sign.SignatureData signatureData = EccAlgorithm.signMessage("123",ecKeyPair);
        String sign = AlgorithmHandler.signMessageStr("123",ecKeyPair.getPrivateKey().toString(16));
        Assert.assertTrue(AlgorithmHandler.verifySignature("123", sign , ecKeyPair.getPublicKey()));
    }
}
