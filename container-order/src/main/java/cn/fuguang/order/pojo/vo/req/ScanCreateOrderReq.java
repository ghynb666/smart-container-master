package cn.fuguang.order.pojo.vo.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class ScanCreateOrderReq implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 请求来源
     */
    @NotEmpty(message = "sourceType not empty")
    private String sourceType;

    /**
     * 客户id
     */
    @NotEmpty(message = "customerId not empty")
    private String customerId;

    /**
     * 设备id
     */
    @NotEmpty(message = "deviceId not empty")
    private String deviceId;

    /**
     * 仓门id
     */
    @NotEmpty(message = "gateId not empty")
    private String gateId;

}
