package cn.fuguang.channel.service.impl;

import cn.fuguang.channel.mapper.OrderInfoMapper;
import cn.fuguang.channel.service.OrderService;
import cn.fuguang.entity.OrderInfoEntity;
import cn.fuguang.exception.ContainerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Override
    public OrderInfoEntity queryByOrderNo(String orderNo) {
        OrderInfoEntity orderInfoEntity = orderInfoMapper.queryByOrderNo(orderNo);

        if (orderInfoEntity == null){
            throw ContainerException.DATE_NOT_EXIST_ERROR.newInstance("订单不存在 orderNo:" + orderNo);
        }
        return orderInfoEntity;
    }
}
