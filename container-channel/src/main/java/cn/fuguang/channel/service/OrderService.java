package cn.fuguang.channel.service;


import cn.fuguang.entity.OrderInfoEntity;
import org.apache.ibatis.annotations.Param;

public interface OrderService {

    /**
     * 根据订单号查询订单实体
     */
    OrderInfoEntity queryByOrderNo(String orderNo);
}
