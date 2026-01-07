package cn.fuguang.order.mapper;

import cn.fuguang.entity.OrderInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderInfoMapper {

    /**
     * 查询商户未支付的订单
     */
    List<OrderInfoEntity> queryUnpaidOrder(@Param("customerId") String customerId);

    void insert(OrderInfoEntity orderInfo);

    OrderInfoEntity queryByOrderNo(@Param("customerId") String orderNo);

    int updateOrderStatus(OrderInfoEntity orderInfoEntity);
}
