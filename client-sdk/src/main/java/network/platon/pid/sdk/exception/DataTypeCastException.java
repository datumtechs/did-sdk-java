package network.platon.pid.sdk.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import network.platon.pid.common.enums.ContractStatusEnum;

@Data
@EqualsAndHashCode(callSuper=false)
public class DataTypeCastException extends RuntimeException {


    private static final long serialVersionUID = 1L;

    private Integer errCode;
    private String errMessage;

    public DataTypeCastException(Integer errCode, String errMessage) {
        super(errMessage);
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    public DataTypeCastException(ContractStatusEnum contractStatusEnum) {
        super(contractStatusEnum.getDesc());
        this.errCode = contractStatusEnum.getCode();
        this.errMessage = contractStatusEnum.getDesc();
    }
}
