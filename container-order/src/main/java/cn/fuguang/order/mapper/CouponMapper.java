package cn.fuguang.order.mapper;

import cn.fuguang.entity.CouponConfigEntity;

public interface CouponMapper {

    /**
     * 根据优惠券配置ID获取优惠券信息
     * @param couponConfigId 优惠券配置ID
     * @return 优惠券配置信息
     */
    CouponConfigEntity selectByCouponConfigId(String couponConfigId);

    /**
     * 更新优惠券已发放数量
     * @param couponConfigId 优惠券配置ID
     * @return 更新结果
     */
    int updateIssuedCount(String couponConfigId);
}
