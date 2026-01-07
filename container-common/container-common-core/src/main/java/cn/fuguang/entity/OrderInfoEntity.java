package cn.fuguang.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderInfoEntity {

    /**
     * ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 客户id
     */
    private String customerId;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 仓门id
     */
    private String gateId;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 优惠券抵扣金额
     */
    private BigDecimal couponAmount;

    /**
     * 交易时间
     */
    private Date trxTime;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 订单状态
     */
    private String orderStatus;

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

    /**
     * 小程序类型
     */
    private String procedureType;


    /**
     * 之前的状态 不是数据库字段
     */
    private String oldOrderStatus;

}
