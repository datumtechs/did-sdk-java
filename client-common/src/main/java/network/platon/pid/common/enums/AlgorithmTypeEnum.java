package network.platon.pid.common.enums;

public enum AlgorithmTypeEnum {

    
	ECC(1,"Ecc Algorithm"),
	;

    private String desc;

    private int code;

    AlgorithmTypeEnum(int code, String desc){
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
    public static AlgorithmTypeEnum getEnumByCodeValue(int code){
    	AlgorithmTypeEnum[] allEnums = values();
        for(AlgorithmTypeEnum enableStatus : allEnums){
            if(enableStatus.getCode()==code)
                return enableStatus;
        }
        return null;
    }
}
