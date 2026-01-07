package cn.fuguang.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BlackCustomerEntity {

    /**
     * id
     */
    private Long id;

    /**
     * 客户id
     */
    private String customerId;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
