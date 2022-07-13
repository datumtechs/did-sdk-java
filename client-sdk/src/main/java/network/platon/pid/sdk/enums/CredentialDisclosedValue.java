package network.platon.pid.sdk.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum CredentialDisclosedValue {

    /**
     * the field is existed.
     */
    EXISTED(2),

    /**
     * the field is disclosed.
     */
    DISCLOSED(1),

    /**
     * the field is not disclosed.
     */
    NOT_DISCLOSED(0);

    private Integer status;

    CredentialDisclosedValue(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
    public static Boolean checkStatus(Integer status) {
    	if(ENUMS.containsKey(status)) {
    		return true;
    	}
    	return false;
    }
    private static final Map <Integer, CredentialDisclosedValue> ENUMS = new HashMap <>();
    static {
        Arrays.asList(CredentialDisclosedValue.values()).forEach(en -> ENUMS.put(en.getStatus(), en));
    }
    
}
