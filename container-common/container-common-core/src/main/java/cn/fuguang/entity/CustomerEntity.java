package cn.fuguang.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerEntity {

    /**
     * 主键id
     */
    private String id;

    /**
     * 客户id
     */
    private String customerId;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 微信openId
     */
    private String wechatOpenId;

    /**
     * 阿里openId
     */
    private String aliOpenId;

    /**
     * 电话
     */
    private String mobile;

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

    /**
     * 备注
     */
    private String remarks;


}
