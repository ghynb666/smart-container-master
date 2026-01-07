package cn.fuguang.api.device.req;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CheckDeviceStatusReqDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 仓门id
     */
    private String gateId;

    /**
     * 设备id
     */
    private String deviceId;
}
