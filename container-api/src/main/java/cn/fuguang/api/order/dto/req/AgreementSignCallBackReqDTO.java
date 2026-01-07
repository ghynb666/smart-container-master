package cn.fuguang.api.order.dto.req;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AgreementSignCallBackReqDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 签约状态
     */
    private String agreementStatus;
}
