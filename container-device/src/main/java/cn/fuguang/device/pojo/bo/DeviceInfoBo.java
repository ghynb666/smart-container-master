package cn.fuguang.device.pojo.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceInfoBo {

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 设备状态
     */
    private String status;
}
