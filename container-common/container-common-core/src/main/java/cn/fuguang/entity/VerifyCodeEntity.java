package cn.fuguang.entity;

import lombok.Data;

import java.util.Date;

@Data
public class VerifyCodeEntity {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 重试次数
     */
    private Integer retryTimes;

    /**
     * 失效时间
     */
    private Date invalidTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
