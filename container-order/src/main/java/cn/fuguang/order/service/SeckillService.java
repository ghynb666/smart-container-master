package cn.fuguang.order.service;

import cn.fuguang.entity.CouponConfigEntity;

public interface SeckillService {

    /**
     * 根据优惠券配置ID获取优惠券信息
     * @param couponConfigId 优惠券配置ID
     * @return 优惠券配置信息
     */
    CouponConfigEntity getCouponConfig(String couponConfigId);

    /**
     * 初始化秒杀优惠券到Redis
     * @param couponConfig 优惠券配置信息
     */
    void initSeckillCoupon(CouponConfigEntity couponConfig);

    /**
     * 处理秒杀成功后的优惠券发放
     * @param customerId 用户ID
     * @param couponConfigId 优惠券配置ID
     * @param orderNo 订单号
     */
    void processSeckillSuccess(String customerId, String couponConfigId, String orderNo);
}
