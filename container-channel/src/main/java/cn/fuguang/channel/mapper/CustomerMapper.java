package cn.fuguang.channel.mapper;

import cn.fuguang.entity.CustomerEntity;
import org.apache.ibatis.annotations.Param;

public interface CustomerMapper {

    /**
     * 根据id查询客户信息
     */
    CustomerEntity queryCustomerById(@Param("customerId") String customerId);

    CustomerEntity queryCustomerByAliOpenId(@Param("alipayUserId") String alipayUserId);
}
