package network.platon.pid.sdk.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum CredentialStatus {

    /**
     * the field is valid.
     */
    VALID(0l, "Valid"),

    /**
     * the field is invalid.
     */
    INVALID(1l, "Invalid");

    private Long status;

    private String desc;

    CredentialStatus(Long status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static boolean checkFail(Long status){
        if(status.compareTo(VALID.status) != 0){
            return true;
        }
        return false;
    }

    public static CredentialStatus getStatusData(Long status){
        return ENUMS.get(status);
    }

    public Long getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    private static final Map <Long, CredentialStatus> ENUMS = new HashMap <>();
    static {
        Arrays.asList(CredentialStatus.values()).forEach(en -> ENUMS.put(en.getStatus(), en));
    }
    
}
