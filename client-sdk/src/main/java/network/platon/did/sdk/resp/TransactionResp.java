package network.platon.did.sdk.resp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.did.common.enums.RetEnum;
import network.platon.did.contract.dto.TransactionInfo;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-09 11:04
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TransactionResp<T> extends BaseResp<T>{

    /**
     * Blockchain transaction information.
     * Only after the transaction is chained will it not be equal to null.
     */
    private TransactionInfo transactionInfo = null;

    public TransactionResp(RetEnum retEnum, T data, TransactionInfo transactionInfo){
        super(retEnum.getCode(), retEnum.getDesc(), data);
        this.transactionInfo = transactionInfo;
    }

    public TransactionResp(Integer code, String errMsg, T data, TransactionInfo transactionInfo){
        super(code, errMsg, data);
        this.transactionInfo = transactionInfo;
    }

    public TransactionResp(Integer code, String errMsg, T data){
        super(code, errMsg, data);
    }

    public static <T> TransactionResp<T> buildSuccess(T data){
        return buildTxSuccess(data, null);
    }

    public static <T> TransactionResp<T> buildTxSuccess(TransactionInfo transactionInfo){
        return buildTxSuccess(null, transactionInfo);
    }

    public static <T> TransactionResp<T> buildTxSuccess(T data, TransactionInfo transactionInfo){
        return new TransactionResp<T>(RetEnum.RET_SUCCESS, data, transactionInfo);
    }

    public static <T> TransactionResp<T> buildNullSuccess(){
        return buildSuccess(null);
    }

    public static <T> TransactionResp<T> build(RetEnum retEnum){
        return buildTx(retEnum, null);
    }

    public static <T> TransactionResp<T> buildTx(RetEnum retEnum, TransactionInfo transactionInfo){
        return new TransactionResp<T>(retEnum, null, transactionInfo);
    }

    public static <T> TransactionResp<T> build(RetEnum retEnum,String message){
        return new TransactionResp<T>(retEnum.getCode(),retEnum.getDesc() + " [" + message + "]", null);
    }

    public static <T> TransactionResp<T> build(RetEnum retEnum,String message, T data){
        return new TransactionResp<T>(retEnum.getCode(),retEnum.getDesc() + " [" + message + "]", data);
    }

    public static <T> TransactionResp<T> buildWith(Integer code, String errMsg){
        return TransactionResp.buildWith(code, errMsg,  null,  null);
    }

    public static <T> TransactionResp<T> buildWith(Integer code, String errMsg, T data, TransactionInfo transactionInfo){
        return new TransactionResp<T>(code, errMsg,  data,  transactionInfo);
    }

    





}
