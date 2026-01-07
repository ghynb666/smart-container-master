package cn.fuguang.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerAgreementSignEntity {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 客户id
     */
    private String customerId;

    /**
     * 签约状态
     */
    private String status;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付宝系统中用以唯一标识用户签约记录的编号
     */
    private String agreementNo;

    /**
     * 签约时间
     */
    private Date signTime;

    /**
     * 阿里openId
     */
    private String aliOpenId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
