package cn.fuguang.order.service;

import cn.fuguang.entity.CustomerEntity;
import org.apache.ibatis.annotations.Param;

public interface CustomerService {

    /**
     * 根据id查询客户信息
     */
    CustomerEntity queryCustomerById(String id);

    /**
     * 校验客户是否存在
     */
    void checkCustomerExist(String customerId);

    /**
     * 通过支付宝openId查询用户信息
     */
    CustomerEntity queryCustomerByAliOpenId(String openId);
}
