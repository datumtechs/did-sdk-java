package network.platon.did.csies.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256 {

    public static BigInteger byteToUin64(byte[] input){

        byte[] array = new byte[8];
        if(input.length > 8){
            System.arraycopy(input, 0, array, 0, array.length);
        }else{
            array = input;
        }

        BigInteger m = new BigInteger(1, array);

        return m;
    }

    public static byte[] uint64ToByte(BigInteger input){

        byte[] array = input.toByteArray();
        if(array.length < 8){
            byte[] tmp = new byte[8];
            System.arraycopy(array, 0, tmp, 8-array.length, array.length);
            array = tmp;
        }
        if(array.length > 8){
            byte[] tmp = new byte[8];
            System.arraycopy(array, 1, tmp, 0, 8);
            array = tmp;
        }
        return array;
    }

    public static byte[] sha256(byte[] input) {
        byte[] result = null;
        try {
            MessageDigest  messageDigest = MessageDigest.getInstance("SHA-256");
            result = messageDigest.digest(input);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

}
