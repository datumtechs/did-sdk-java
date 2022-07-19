package network.platon.pid.common.enums;

/**
 *  Ret Code enum
 *  @file RetEnum.java
 *  @description 
 *	@author zhangrj
 */
public enum RetEnum {

    /** Definition of business error codes*/

    /**
     * The error code rule
     *
     * 1num|2num|3num
     * {error type}|{mudule}|{code}
     * {1-9}|{00-99}|{000-999}
     *
     * error type:
     *      sys error: 1
     *      business error: 2
     *
     * module:
     *      common: 00
     *      PID: 01
     *      pct: 02
     *      credential: 03
     *      evidence: 04
     *      presentation: 05
     *      issuer (authority or role): 06
     *
     * code:
     *      Customized according to different business
     * */

    RET_SUCCESS(0,"success"),


    /**
     * system error codes ...
     */
    RET_SYS_ERROR(100000, "SYS ERROR!!"),
    RET_DEPLOY_CONTRACT_ERROR(100001, "deploy contract error!!"),
    RET_DATA_CAST_ERROR(100002, "the data cast failed!!"),


    /**
     * business error codes ...
     */
    // about common
    RET_COMMON_CREATE_KEY_ERROR(200000,"Failed to create  publicKey and privateKey"),
    RET_COMMON_PUBLICKEY_AND_PRIVATEKEY_NOT_MATCHED(200001,"Failed to match  publicKey and privateKey"),
    RET_COMMON_PARAM_INVALLID(200002,"Request params is invalid"),
    RET_COMMON_PRIVATEKEY_INVALID (200003, "the input private key is invalid, please check and input your private key"),
    RET_COMMON_PARAM_PROOF_INVALID(200004,"Request params is invalid.[proof data is invalid]"),
    RET_COMMON_PARAM_CLAIMMATE_INVALID(200005,"Request params is invalid.[claimmate data is invalid]"),


    // about PID
    RET_PID_IDENTITY_CALL_CONTRACT_ERROR(201000, "Failed to call PIDContract"),
    RET_PID_CREATE_PID_ERROR(201001, "Failed to create PlatON DID"),
    RET_PID_IDENTITY_NOTEXIST (201002, "The identity is not exist"),
    RET_PID_IDENTITY_ALREADY_REVOCATED(201003, "The identity has bean revocated"),
    RET_PID_IDENTITY_ALREADY_EXIST(201004, "The identity is already exist"),
    RET_PID_QUERY_DOCUMENT_ERROR (201005, "Failed to query document info"),
    RET_PID_INVALID(201006, "The pid is illegal"),
    RET_PID_ADD_PUBLICKEY_ERROR(201007, "Failed to add public key"),
    RET_PID_PUBLICKEY_ALREADY_EXIST(201008, "The public key is already exist"),
    RET_PID_PUBLICKEY_NOTEXIST(201009, "The public key is not exist"),
    RET_PID_UPDATE_PUBLICKEY_ERROR(201010, "Failed to update public key"),
    RET_PID_PUBLICKEY_ALREADY_REVOCATED(201011, "The public key has bean revocated"),
    RET_PID_CONNOT_REVOCATION_LAST_PUBLICKEY(201012, "Can not revocation the last public key"),
    RET_PID_UPDATE_PUBLICKEY_SAME(201013, "the public key is the same"),
    RET_PID_SET_SERVICE_ALREADY_EXIST(201015, "The service is already exist"),
    RET_PID_SET_SERVICE_NOTEXIST(201016, "The service is not exist"),
    RET_PID_SET_SERVICE_ALREADY_REVOCATED(201017, "The service has bean revocated"),
    RET_PID_SET_STATUS_ERROR(201018, "Failed to set status for document"),
    RET_PID_SET_AUTHENTICATION_NOTEXIST(201019, "The authentication is not exist"),


    // about PCT
    RET_PCT_JSON_SCHEMA_ERROR(202000,"PCT json schema not valid"),
    RET_PCT_QUERY_BY_ID_ERROR(202001,"PCT query json error when requesting contract call"),
    RET_PCT_REGISTER_PIT_ERROR(202002,"PCT register error."),
    RET_PCT_REGISTER_CNTRACNT_ERROR(202003,"PCT register contract error."),
    RET_PCT_QUERY_PCT_DATA_ERROR(202004,"PCT not found PCT data contract."),
    RET_PCT_QUERY_JSON_NOT_FOUND_ERROR(202005,"PCT json not found in contract."),
    RET_PCT_QUERY_ISSUER_NOT_FOUND_ERROR(202006,"PCT issuer not found in contract."),


    // about Credential
    RET_CREDENTIAL_VERIFY_ERROR(203000,"Verify Credential Fail"),
    RET_CREDENTIAL_TRANSFER_MAP_ERROR(203001,"Credential transfer Fail"),
    RET_CREDENTIAL_PID_ERROR(203002,"Credential pid match error"),
    RET_CREDENTIAL_MATCH_PUBLICKEYID_ERROR(203004,"Pid document not found publicKeyId"),
    RET_CREDENTIAL_MATCH_PUB_PRI_ERROR(203005,"Pid publickey not match privatekey"),
    RET_CREDENTIAL_GET_PCT_ERROR(203006,"Get pct json error"),
    RET_CREDENTIAL_PCT_MATCH_ERROR(203007,"Json schema validator failed"),
    RET_CREDENTIAL_CONTRACT_CREATE_ERROR(203008,"Credential contract create error"),
    RET_CREDENTIAL_CONTRACT_QUERTY_ERROR(203009,"Credential contract query error"),
    RET_CREDENTIAL_CONTRACT_NOT_FOUND_ERROR(203010,"Credential contract cannot find the corresponding credential"),
    RET_CREDENTIAL_AUTH_STATUS_INVAILD(203011,"Pid auth status invalid"),
    RET_CREDENTIAL_PUBLICKEY_STATUS_INVAILD(203012,"Pid publickey status invalid"),
    RET_CREDENTIAL_PUBLICKEY_NOT_AUTH(203014,"Unauthorized disclosure of pid's public key"),
    RET_CREDENTIAL_PID_NOT_FOUND(203015,"Not found pid in contract"),
    RET_CREDENTIAL_GET_STATUS_FAIL(203016,"Credential contract cannot find the status of credential"),
    RET_CREDENTIAL_CHANGE_STATUS_FAIL(203017,"Credential contract cannot change the status of credential"),
    RET_CREDENTIAL_EXPIRED(203018,"Credential has expired"),


    // about Evidence
    RET_EVIDENCE_VERIFY_ERROR(204001,"Evidence verify data fail"),
    RET_EVIDENCE_EXIST_ERROR(204002,"Evidence is exist"),
    RET_EVIDENCE_NOT_EXIST_ERROR(204003,"Evidence is not exist"),
    RET_EVIDENCE_STATUS_INVALID(204004,"Evidence status is invalid"),
    RET_EVIDENCE_NO_OPERATION_ERROR(204005,"No evidence operation authority"),

    // about Presentation
    RET_PRESENTATION_CLAIM_ERROR(205001,"Presentation claim not contains key"),
    RET_PRESENTATION_SALT_ERROR(205002,"Presentation salt not contains key"),
    RET_PRESENTATION_POLICY_DISCLOSUREVALUE_ILLEGAL(205003,"Presentation policy disclosureValue  illegal."),
    RET_PRESENTATION_DISCLOSUREVALUE_NOTMATCH_SALTVALUE(205004,"Presentation disclosureValue  not match salt value."),
    RET_PRESENTATION_VERIFY_ERROR(205005,"Verify presentation proof fail"),


    // about vote
    RET_VOTE_CALL_CONTRACT_ERROR(206000, "Failed to call vote Contract"),

    ;
    // ------------------------
    private String desc;

    private int code;

    RetEnum(int code, String desc){
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
    public static RetEnum getEnumByCodeValue(int code){
        RetEnum[] allEnums = values();
        for(RetEnum enableStatus : allEnums){
            if(enableStatus.getCode()==code)
                return enableStatus;
        }
        return null;
    }

    public static boolean isSuccess(RetEnum retEnum) {
    	if(retEnum.getCode() == RET_SUCCESS.getCode()) {
    		return true;
    	}
    	return false;
    }
    
    public static boolean isFail(RetEnum retEnum) {
    	if(retEnum.getCode() != RET_SUCCESS.getCode()) {
    		return true;
    	}
    	return false;
    }
    
    public static boolean isFailCode(int code) {
    	if(code != RET_SUCCESS.getCode()) {
    		return true;
    	}
    	return false;
    }
}