package cn.fuguang.api.device.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateOrderOpenGateReqDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 仓门id
     */
    private String gateId;

    /**
     * 设备id
     */
    private String deviceId;
}
