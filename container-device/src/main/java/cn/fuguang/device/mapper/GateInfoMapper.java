package cn.fuguang.device.mapper;

import cn.fuguang.entity.GateInfoEntity;
import org.apache.ibatis.annotations.Param;

public interface GateInfoMapper {

    /**
     * 根据设备id查询仓门实体
     */
    GateInfoEntity queryGateInfoById(@Param("gateId") String gateId);
}
