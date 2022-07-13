package network.platon.pid.sdk.enums;

/**
 * @Description: The Policy Type Enum for Presentation
 * @Author: Gavin
 * @Date: 2020-06-05 16:43
 */
public enum PresentationPolicyType {

    POLICY_DEFAULT(0,"DEFAULT"),
    ;

    private String desc;

    private int type;

    PresentationPolicyType(int type, String desc){
        this.desc = desc;
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }
    public int getType() {
        return type;
    }

}
