package network.platon.did.csies.utils;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

public class Sha256Test {

    @Test
    public void testSequence() {
        BigInteger seed = new BigInteger("23523865082340324");
        byte[] array = Sha256.uint64ToByte(seed);
        for(int i = 0; i < 10; i++){
            // System.out.println(Arrays.toString(array));
            byte[] tmp = Sha256.sha256(array);
            System.out.println(Sha256.byteToUin64(tmp));
            array = tmp;
        }
    }
}
