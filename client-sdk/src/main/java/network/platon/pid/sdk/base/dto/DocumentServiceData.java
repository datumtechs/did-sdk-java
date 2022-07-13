package network.platon.pid.sdk.base.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Author: Gavin
 * @Date: 2020-06-12 13:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DocumentServiceData extends PidService{

    private String status;
}
