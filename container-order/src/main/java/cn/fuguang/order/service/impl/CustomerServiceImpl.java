package cn.fuguang.order.service.impl;

import cn.fuguang.entity.CustomerEntity;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.order.mapper.CustomerMapper;
import cn.fuguang.order.service.CustomerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private CustomerMapper customerMapper;

    @Override
    public CustomerEntity queryCustomerById(String id) {
        CustomerEntity customerEntity = customerMapper.queryCustomerById(id);

        if (customerEntity == null){
            throw ContainerException.DATE_NOT_EXIST_ERROR.newInstance("客户不存在 id:" + id);
        }
        return customerEntity;
    }

    @Override
    public void checkCustomerExist(String customerId) {
        this.queryCustomerById(customerId);
    }

    @Override
    public CustomerEntity queryCustomerByAliOpenId(String openId) {
        CustomerEntity customerEntity = customerMapper.queryCustomerByAliOpenId(openId);

        if (customerEntity == null){
            throw ContainerException.DATE_NOT_EXIST_ERROR.newInstance("客户不存在 openId:" + openId);
        }
        return customerEntity;
    }
}
