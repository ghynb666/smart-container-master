package cn.fuguang.order.mapper;

import cn.fuguang.entity.CustomerEntity;
import org.apache.ibatis.annotations.Param;

public interface CustomerMapper {

    /**
     * 根据id查询客户信息
     */
    CustomerEntity queryCustomerById(@Param("id") String id);

    /**
     * 根据aliOpenId查询客户信息
     */
    CustomerEntity queryCustomerByAliOpenId(@Param("openId") String openId);
}
