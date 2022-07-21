package network.platon.pid.common.enums;

public enum ContractStatusEnum {

    /** Definition of business error codes*/ // the “C” mean a contract
    
	C_STATUS_SUCCESS(0,"SUCCESS"),
	C_NETWOEK_EXCEPTION(-1,"failed to connect chain with web3j"),
	C_EVENT_NULL(-2,"the event is null on receipt"),
	C_RES_NULL(-3,"the res is null on log in event of receipt"),
	C_EVENTLOG_NULL(-4,"the log is null on event of receipt"),
	C_KEY_NOT_MATCH(-5,"the key haven't match in log of event"),
    C_VALUE_NOT_MATCH(-6,"the value haven't match in log of event"),
	;

    private String desc;

    private int code;

    ContractStatusEnum(int code, String desc){
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
    public static ContractStatusEnum getEnumByCodeValue(int code){
        ContractStatusEnum[] allEnums = values();
        for(ContractStatusEnum enableStatus : allEnums){
            if(enableStatus.getCode() == code)
                return enableStatus;
        }
        return null;
    }
}
