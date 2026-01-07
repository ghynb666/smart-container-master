package cn.fuguang.order.mapper;

import cn.fuguang.entity.GateInfoEntity;
import cn.fuguang.entity.OrderInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GateInfoMapper {

    /**
     * 根据设备id查询仓门实体
     */
    GateInfoEntity queryGateInfoById(@Param("gateId") String gateId);
}
