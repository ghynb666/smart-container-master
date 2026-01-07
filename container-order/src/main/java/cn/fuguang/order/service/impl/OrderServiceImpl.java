package cn.fuguang.order.service.impl;

import cn.fuguang.constants.BaseConstants;
import cn.fuguang.entity.CustomerEntity;
import cn.fuguang.entity.OrderInfoEntity;
import cn.fuguang.enums.OrderStatusEnum;
import cn.fuguang.enums.OrderTypeEnum;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.order.mapper.OrderInfoMapper;
import cn.fuguang.order.pojo.vo.req.ScanCreateOrderReq;
import cn.fuguang.order.service.OrderService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderInfoMapper orderInfoMapper;


    @Override
    public void checkCustomerUnpaidOrder(String customerId) {
        List<OrderInfoEntity> list = orderInfoMapper.queryUnpaidOrder(customerId);
        if (!list.isEmpty()){
            throw ContainerException.CUSTOMER_UNPAID_ERROR;
        }
    }

    @Override
    public String createOrderNo(OrderTypeEnum orderType) {
        Date nowDate = new Date();
        String dateStr = DateUtil.format(nowDate, BaseConstants.DATE_PATTEN_STR);
        return orderType + dateStr + RandomUtil.randomNumbers(6);
    }

    @Override
    public OrderInfoEntity buildOrderInfo(ScanCreateOrderReq req, String orderNo) {
        OrderInfoEntity orderInfo = new OrderInfoEntity();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setCustomerId(req.getCustomerId());
        orderInfo.setDeviceId(req.getDeviceId());
        orderInfo.setGateId(req.getGateId());
        orderInfo.setTrxTime(new Date());
        orderInfo.setProcedureType(req.getSourceType());
        orderInfo.setOrderStatus(OrderStatusEnum.INIT.name());
        return orderInfo;
    }

    @Override
    public void saveOrderInfo(OrderInfoEntity orderInfo) {
        try {
            orderInfoMapper.insert(orderInfo);
        } catch (Exception e) {
            throw ContainerException.DATABASE_INERT_ERROR;
        }
    }

    @Override
    public void updateOrderStatus(String orderNo, String inputStatus) {

        boolean updateFlag = false;
        int updateTimes = 0;

        while (!updateFlag && updateTimes < BaseConstants.UPDATE_ORDER_STATUS_MAX_RETRY){
            OrderInfoEntity orderInfoEntity = this.queryByOrderNo(orderNo);
            String updateStatus = OrderStatusEnum.nextStatus(orderInfoEntity.getOrderStatus(), inputStatus);
            OrderInfoEntity updateEntity = new OrderInfoEntity();
            updateEntity.setOrderNo(orderNo);
            updateEntity.setOldOrderStatus(orderInfoEntity.getOrderStatus());
            updateEntity.setOrderStatus(updateStatus);
            int result = orderInfoMapper.updateOrderStatus(orderInfoEntity);
            if (result == 1){
                updateFlag = true;
            } else {
                updateTimes++;
            }
        }

        if (!updateFlag){
            throw ContainerException.DATABASE_UPDATE_ERROR.newInstance("订单:{}更新订单状态失败,重试超过最大值,需要更新的状态为:{}", orderNo, inputStatus);
        }

    }

    @Override
    public OrderInfoEntity queryByOrderNo(String orderNo) {
        OrderInfoEntity orderInfo = orderInfoMapper.queryByOrderNo(orderNo);

        if (orderInfo == null){
            throw ContainerException.DATE_NOT_EXIST_ERROR.newInstance("订单不存在 orderNo:" + orderNo);
        }
        return orderInfo;
    }
}
