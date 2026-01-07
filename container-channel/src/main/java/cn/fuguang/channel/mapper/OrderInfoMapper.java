package cn.fuguang.channel.mapper;

import cn.fuguang.entity.OrderInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderInfoMapper {

    /**
     * 根据订单号查询订单实体
     */
    OrderInfoEntity queryByOrderNo(@Param("orderNo") String orderNo);
}
