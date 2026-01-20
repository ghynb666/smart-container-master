package cn.fuguang.order.service.impl;

import cn.fuguang.common.redis.service.RedisService;
import cn.fuguang.constants.RedisConstants;
import cn.fuguang.entity.CouponConfigEntity;
import cn.fuguang.order.mapper.CouponMapper;
import cn.fuguang.order.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private RedisService redisService;

    @Override
    public CouponConfigEntity getCouponConfig(String couponConfigId) {
        return couponMapper.selectByCouponConfigId(couponConfigId);
    }

    @Override
    public void initSeckillCoupon(CouponConfigEntity couponConfig) {
        if (couponConfig == null || !"1".equals(couponConfig.getIsSeckill())) {
            return;
        }

        String couponConfigId = couponConfig.getCouponConfigId();
        
        // 1. 缓存优惠券基本信息
        String infoKey = RedisConstants.SECKILL_COUPON_INFO + couponConfigId;
        redisService.hset(infoKey, "couponConfigId", couponConfig.getCouponConfigId());
        redisService.hset(infoKey, "couponName", couponConfig.getCouponName());
        redisService.hset(infoKey, "couponMoney", couponConfig.getCouponMoney());
        redisService.hset(infoKey, "couponCount", couponConfig.getCouponCount().toString());
        redisService.hset(infoKey, "seckillStartTime", couponConfig.getSeckillStartTime().getTime() + "");
        redisService.hset(infoKey, "seckillEndTime", couponConfig.getSeckillEndTime().getTime() + "");
        
        // 2. 设置库存
        String stockKey = RedisConstants.SECKILL_COUPON_STOCK + couponConfigId;
        redisService.set(stockKey, couponConfig.getCouponCount().toString());
        
        // 3. 设置过期时间（秒杀结束后24小时过期）
        long expireTime = (couponConfig.getSeckillEndTime().getTime() - System.currentTimeMillis()) / (1000 * 60) + 24 * 60;
        redisService.expire(infoKey, expireTime);
        redisService.expire(stockKey, expireTime);
        
        log.info("初始化秒杀优惠券成功，couponConfigId: {}", couponConfigId);
    }

    @Override
    public void processSeckillSuccess(String customerId, String couponConfigId, String orderNo) {
        try {
            // 1. 更新优惠券已发放数量
            int updateResult = couponMapper.updateIssuedCount(couponConfigId);
            if (updateResult > 0) {
                log.info("更新优惠券已发放数量成功，couponConfigId: {}, customerId: {}", couponConfigId, customerId);
            } else {
                log.error("更新优惠券已发放数量失败，couponConfigId: {}, customerId: {}", couponConfigId, customerId);
                return;
            }
            
            // 2. 这里可以添加发放优惠券给用户的逻辑
            // 例如：插入coupon_customer表记录
            
            log.info("处理秒杀成功优惠券发放完成，orderNo: {}, customerId: {}, couponConfigId: {}", 
                    orderNo, customerId, couponConfigId);
            
        } catch (Exception e) {
            log.error("处理秒杀成功优惠券发放异常，orderNo: {}, customerId: {}, couponConfigId: {}", 
                    orderNo, customerId, couponConfigId, e);
        }
    }
}
