package cn.fuguang.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponConfigEntity {

    private Long id;

    /**
     * 优惠券配置id
     */
    private String couponConfigId;

    /**
     * 商品id
     */
    private String productUid;

    /**
     * 优惠券类型
     */
    private String couponType;

    /**
     * 优惠券金额 现金券字段
     */
    private BigDecimal couponMoney;

    /**
     * 现金券是否全品类 （现金券字段）
     */
    private String couponAllCategories;

    /**
     * 优惠券数量
     */
    private Long couponCount;

    /**
     * 优惠券有效期 单位天
     */
    private Long couponValidDate;

    /**
     * 优惠券图片
     */
    private String couponImage;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 领取有效期 单位天
     */
    private Long couponReceiveValidDate;

    /**
     * 优惠券总金额
     */
    private BigDecimal couponTotalMoney;

    /**
     * 总用户限制
     */
    private Long couponCustomerLimit;

    /**
     * 单用户单次优惠券总数量限制
     */
    private Long couponCustomerAmountLimit;

    /**
     * 已发放张数
     */
    private Long couponIssuedCount;

    /**
     * 团购截止时间 (团购券字段)
     */
    private Date couponDeadline;

    /**
     * 券说明
     */
    private String couponDesc;

    /**
     * 备注
     */
    private String remarks;

    private Date createTime;

}
