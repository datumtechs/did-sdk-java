package network.platon.pid.sdk.resolve.dto;

import java.math.BigInteger;

import lombok.Data;
import network.platon.pid.common.enums.ContractStatusEnum;


@Data
public class DecodeResult {

    private ContractStatusEnum resultStatus;

    private BigInteger previousBlock;

    private static final BigInteger PREVIOUS_BLOCK = BigInteger.ZERO;

    public void setDecodeEventLogStatus(ContractStatusEnum status) {

        if (!ContractStatusEnum.C_STATUS_SUCCESS.equals(status)) {
            this.previousBlock = PREVIOUS_BLOCK;
        }
        this.resultStatus = status;
    }
}
