package cn.fuguang.order.service.impl;

import cn.fuguang.entity.BlackCustomerEntity;
import cn.fuguang.enums.ActiveStatusEnum;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.order.mapper.BlackCustomerMapper;
import cn.fuguang.order.service.BlackCustomerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BlackCustomerServiceImpl implements BlackCustomerService {

    @Resource
    private BlackCustomerMapper blackCustomerMapper;

    @Override
    public void checkCustomer(String customerId) {
        BlackCustomerEntity blackCustomerEntity = blackCustomerMapper.queryBlackCustomer(customerId);
        if (blackCustomerEntity != null && ActiveStatusEnum.ACTIVE.name().equals(blackCustomerEntity.getStatus())){
            throw ContainerException.BLACK_CUSTOMER_ERROR;
        }
    }
}
