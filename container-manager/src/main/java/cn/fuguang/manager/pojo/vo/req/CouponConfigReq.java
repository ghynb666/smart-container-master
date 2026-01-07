package cn.fuguang.manager.pojo.vo.req;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CouponConfigReq implements Serializable {

    // TODO: 2024/8/6 非空注解到时候在做

    private static final long serialVersionUID = -1L;

    /**
     * 商品id
     */
    private String productUid;

    /**
     * 优惠券类型
     */
    private String couponType;

    /**
     * 优惠券金额
     */
    private BigDecimal couponMoney;

    /**
     * 现金券是否全品类
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
     * 领取有效期
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
    private String couponDeadline;

    /**
     * 券说明
     */
    private String couponDesc;

    /**
     * 备注
     */
    private String remarks;
}
