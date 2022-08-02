package network.platon.did.sdk.enums;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum CredentialStatus {

    /**
     * the field is valid.
     */
    VALID(BigInteger.ZERO, "Valid"),

    /**
     * the field is invalid.
     */
    INVALID(BigInteger.ONE, "Invalid");

    private BigInteger status;

    private String desc;

    CredentialStatus(BigInteger status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static boolean checkFail(BigInteger status){
        if(status.compareTo(VALID.status) != 0){
            return true;
        }
        return false;
    }

    public static CredentialStatus getStatusData(BigInteger status){
        return ENUMS.get(status);
    }

    public BigInteger getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    private static final Map <BigInteger, CredentialStatus> ENUMS = new HashMap <>();
    static {
        Arrays.asList(CredentialStatus.values()).forEach(en -> ENUMS.put(en.getStatus(), en));
    }
    
}
