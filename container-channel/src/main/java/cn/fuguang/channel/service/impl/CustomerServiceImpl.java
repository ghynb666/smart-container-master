package cn.fuguang.channel.service.impl;

import cn.fuguang.channel.mapper.CustomerMapper;
import cn.fuguang.channel.service.CustomerService;
import cn.fuguang.entity.CustomerEntity;
import cn.fuguang.exception.ContainerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private CustomerMapper customerMapper;

    @Override
    public CustomerEntity queryCustomerById(String customerId) {
        CustomerEntity customerEntity = customerMapper.queryCustomerById(customerId);

        if (customerEntity == null){
            throw ContainerException.DATE_NOT_EXIST_ERROR.newInstance("客户不存在 id:" + customerId);
        }
        return customerEntity;
    }

    @Override
    public CustomerEntity queryCustomerByAliOpenId(String alipayUserId) {
        CustomerEntity customerEntity = customerMapper.queryCustomerByAliOpenId(alipayUserId);

        if (customerEntity == null){
            throw ContainerException.DATE_NOT_EXIST_ERROR.newInstance("客户不存在 alipayUserId:" + alipayUserId);
        }
        return customerEntity;

    }

}
