package network.platon.did.common.enums;

public enum DataTypeCastEnum {

    /** Definition of business error codes*/

    DATATYPECAST_SUCCESS(0, "success"),
    DATATYPECAST_STR2JSON_FAILED(1, "failed to cast string to json");

    private String desc;

    private int code;

    DataTypeCastEnum(int code, String desc){
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }
    public int getCode() {
        return code;
    }

    /**
     * Query enumeration based on code
     * @param code
     * @return
     */
    public static DataTypeCastEnum getEnumByCodeValue(int code){
        DataTypeCastEnum[] allEnums = values();
        for(DataTypeCastEnum enableStatus : allEnums){
            if(enableStatus.getCode()==code)
                return enableStatus;
        }
        return null;
    }
}
