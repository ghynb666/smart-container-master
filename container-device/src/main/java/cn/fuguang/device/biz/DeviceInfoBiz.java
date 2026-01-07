package cn.fuguang.device.biz;

import cn.fuguang.api.device.req.CheckDeviceStatusReqDTO;

public interface DeviceInfoBiz {

    /**
     * 校验设备状态
     */
    void checkDeviceStatus(CheckDeviceStatusReqDTO reqDTO);

    /**
     * 定时检查心跳状态
     */
    void checkDeviceHeart();
}
