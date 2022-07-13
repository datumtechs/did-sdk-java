package network.platon.pid.sdk.resp;

import network.platon.pid.common.enums.RetEnum;

/**
 *  Base response message
 *  @file BaseResp.java
 *  @description
 *	@author Rongjin Zhang
 */
public class BaseResp<T> {
    protected String errMsg;
    protected Integer code;
    protected T data;


    public BaseResp() {
    	this.code=0;
    }

    public BaseResp(Integer code, String errMsg, T data){
        this.code=code;
        this.errMsg=errMsg;
        this.data=data;
    }

    public String getErrMsg() {
        return errMsg;
    }

    /** static function , init return object */
    public static <T> BaseResp<T> build(Integer code, String errMsg){
        return BaseResp.build(code,errMsg, null);
    }

    public static <T> BaseResp<T> build(Integer code, String errMsg, T data){
        return new BaseResp<>(code,errMsg, data);
    }

    public static <T> BaseResp<T> build(RetEnum retEnum){
        return build(retEnum.getCode(),retEnum.getDesc(), null);
    }

    public static <T> BaseResp<T> build(RetEnum retEnum, T data){
        return build(retEnum.getCode(),retEnum.getDesc(), data);
    }

    public static <T> BaseResp<T> build(RetEnum retEnum,String message){
        return build(retEnum.getCode(),retEnum.getDesc() + " [" + message + "]");
    }

    public static <T> BaseResp<T> build(RetEnum retEnum,String message, T data){
        return build(retEnum.getCode(),retEnum.getDesc() + " [" + message + "]", data);
    }

    public static <T> BaseResp<T> buildSuccess(T data){
        return build(RetEnum.RET_SUCCESS, data);
    }

    public static <T> BaseResp<T> buildSuccess(){
        return buildSuccess(null);
    }

    public static <T> BaseResp<T> buildException(){
        return build(RetEnum.RET_SYS_ERROR, null);
    }

    public static <T> BaseResp<T> buildError(RetEnum retEnum){
        return build(retEnum, null);
    }

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public boolean checkSuccess() {
		if(this.code.intValue() != RetEnum.RET_SUCCESS.getCode()) {
			return false;
		}
		return true;
	}

    public boolean checkFail() {
       return !this.checkSuccess();
    }

    public  boolean checkEqualCode(RetEnum retEnum) {
        if (this.code.intValue() != retEnum.getCode()) {
            return false;
        }
        return true;
    }

    public  boolean checkNoEqualCode(RetEnum retEnum) {
        return !this.checkEqualCode(retEnum);
    }
}