package cn.fuguang.device.service;

import cn.fuguang.api.device.req.CreateOrderOpenGateReqDTO;
import cn.fuguang.device.pojo.bo.DeviceInfoBo;
import cn.fuguang.entity.DeviceInfoEntity;
import cn.fuguang.enums.DeviceStatusEnum;

import java.util.List;

public interface DeviceService {

    /**
     * 校验货柜状态
     */
    void checkDeviceStatus(String deviceId, String gateId);

    /**
     * 开门前重量缓存
     */
    void doCreateOpenGateOrderCache(CreateOrderOpenGateReqDTO reqDTO);

    /**
     * 根据bo查询设备列表
     */
    List<DeviceInfoEntity> queryByBo(DeviceInfoBo deviceInfoBo);

    /**
     * 变更设备状态
     */
    void updateDeviceStatus(String deviceId, String status, String deviceStatus);

    /**
     * 根据sn号查询设备
     */
    DeviceInfoEntity queryByDeviceSn(String deviceSn);
}
