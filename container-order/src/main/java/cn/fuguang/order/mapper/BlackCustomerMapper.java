package cn.fuguang.order.mapper;

import cn.fuguang.entity.BlackCustomerEntity;
import cn.fuguang.entity.OrderInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BlackCustomerMapper {

    BlackCustomerEntity queryBlackCustomer(@Param("customerId") String customerId);
}
