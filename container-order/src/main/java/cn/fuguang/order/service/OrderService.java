package cn.fuguang.order.service;

import cn.fuguang.entity.OrderInfoEntity;
import cn.fuguang.enums.OrderTypeEnum;
import cn.fuguang.order.pojo.vo.req.ScanCreateOrderReq;

public interface OrderService {

    /**
     * 检查客户是否有未支付的订单
     */
    void checkCustomerUnpaidOrder(String customerId);

    /**
     * 生成订单号
     */
    String createOrderNo(OrderTypeEnum orderType);

    /**
     * 构建订单信息
     */
    OrderInfoEntity buildOrderInfo(ScanCreateOrderReq req, String orderNo);

    /**
     * 保存订单
     */
    void saveOrderInfo(OrderInfoEntity orderInfo);

    /**
     * 更新订单状态
     */
    void updateOrderStatus(String orderNo, String inputStatus);

    /**
     * 根据订单号查询订单实体
     */
    OrderInfoEntity queryByOrderNo(String orderNo);
}
