package cn.fuguang.api.channel.dto.req;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AgreementSignReqDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 客户id
     */
    private String customerId;

    /**
     * 客户手机号
     */
    private String mobile;

}
