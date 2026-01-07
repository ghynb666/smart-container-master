package cn.fuguang.channel.service;

import cn.fuguang.entity.CustomerEntity;

public interface CustomerService {

    /**
     * 根据id查询客户信息
     */
    CustomerEntity queryCustomerById(String id);


    /**
     * 根据阿里openId查询用户信息
     */
    CustomerEntity queryCustomerByAliOpenId(String alipayUserId);
}
