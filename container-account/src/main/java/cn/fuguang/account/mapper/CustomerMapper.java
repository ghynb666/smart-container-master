package cn.fuguang.account.mapper;

import cn.fuguang.entity.CustomerEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CustomerMapper {

    int insert(CustomerEntity record);

    CustomerEntity selectByUsername(@Param("username") String username);

    CustomerEntity selectById(@Param("id") String id);
}
