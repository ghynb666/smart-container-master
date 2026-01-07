package cn.fuguang.device.mapper;

import cn.fuguang.device.pojo.bo.DeviceInfoBo;
import cn.fuguang.entity.DeviceInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeviceInfoMapper {

    /**
     * 根据设备id查询设备实体
     */
    DeviceInfoEntity queryDeviceInfoById(@Param("deviceId") String deviceId);

    /**
     * 根据bo查询设备列表
     */
    List<DeviceInfoEntity> queryByBo(DeviceInfoBo deviceInfoBo);

    int updateDeviceStatus(DeviceInfoEntity updateEntity);

    DeviceInfoEntity queryByDeviceSn(@Param("deviceSn") String deviceSn);
}
