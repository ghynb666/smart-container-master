package cn.fuguang.order.mapper;

import cn.fuguang.entity.DeviceInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeviceInfoMapper {

    /**
     * 根据设备id查询设备实体
     */
    DeviceInfoEntity queryDeviceInfoById(@Param("deviceId") String deviceId);
}
