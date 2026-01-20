package cn.fuguang.order.biz.impl;

import cn.fuguang.common.redis.service.RedisService;
import cn.fuguang.common.rocketmq.service.RocketMqService;
import cn.fuguang.constants.RedisConstants;
import cn.fuguang.constants.RocketMQConstants;
import cn.fuguang.entity.CouponConfigEntity;
import cn.fuguang.order.biz.SeckillBiz;
import cn.fuguang.order.pojo.vo.req.SeckillCouponReq;
import cn.fuguang.order.pojo.vo.res.SeckillCouponRes;
import cn.fuguang.order.service.SeckillService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class SeckillBizImpl implements SeckillBiz {

    @Resource
    private RedisService redisService;

    @Resource
    private RocketMqService rocketMqService;

    @Resource
    private SeckillService seckillService;

    @Override
    public SeckillCouponRes doSeckill(SeckillCouponReq req) {
        SeckillCouponRes res = new SeckillCouponRes();
        
        // 1. 验证请求参数
        if (StringUtils.isAnyEmpty(req.getCustomerId(), req.getCouponConfigId(), req.getVerifyToken())) {
            res.setStatus(2);
            res.setMessage("请求参数无效");
            return res;
        }
        
        String customerId = req.getCustomerId();
        String couponConfigId = req.getCouponConfigId();
        
        // 2. 验证秒杀时间
        CouponConfigEntity couponConfig = seckillService.getCouponConfig(couponConfigId);
        if (couponConfig == null) {
            res.setStatus(2);
            res.setMessage("优惠券不存在");
            return res;
        }
        
        if (!"1".equals(couponConfig.getIsSeckill())) {
            res.setStatus(2);
            res.setMessage("该优惠券不是秒杀券");
            return res;
        }
        
        Date now = new Date();
        if (now.before(couponConfig.getSeckillStartTime()) || now.after(couponConfig.getSeckillEndTime())) {
            res.setStatus(2);
            res.setMessage("秒杀未开始或已结束");
            return res;
        }
        
        // 3. 验证滑块验证码
        // 这里可以根据verifyToken进行更复杂的验证，当前简化处理
        
        // 4. Redis库存预减
        String stockKey = RedisConstants.SECKILL_COUPON_STOCK + couponConfigId;
        Long stock = redisService.decrement(stockKey);
        if (stock == null || stock < 0) {
            // 库存不足，恢复库存
            redisService.increment(stockKey, 1);
            res.setStatus(2);
            res.setMessage("优惠券已抢完");
            return res;
        }
        
        // 5. 验证用户是否已秒杀过该优惠券
        String userKey = RedisConstants.SECKILL_COUPON_USER + couponConfigId;
        Boolean isMember = redisService.sIsMember(userKey, customerId);
        if (isMember != null && isMember) {
            // 用户已秒杀过，恢复库存
            redisService.increment(stockKey, 1);
            res.setStatus(2);
            res.setMessage("您已成功秒杀过该优惠券");
            return res;
        }
        
        try {
            // 6. 生成秒杀订单号
            String orderNo = "SECKILL" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
            
            // 7. 将用户添加到已秒杀列表
            redisService.sAdd(userKey, customerId);
            
            // 8. 构造消息体
            JSONObject msgBody = new JSONObject();
            msgBody.put("orderNo", orderNo);
            msgBody.put("customerId", customerId);
            msgBody.put("couponConfigId", couponConfigId);
            msgBody.put("couponName", couponConfig.getCouponName());
            msgBody.put("couponMoney", couponConfig.getCouponMoney());
            
            // 9. 发送消息到RocketMQ
            rocketMqService.sendMessage(RocketMQConstants.SECKILL_COUPON_TOPIC, msgBody.toJSONString());
            
            // 10. 返回秒杀结果
            res.setOrderNo(orderNo);
            res.setStatus(0);
            res.setMessage("秒杀成功，正在处理中");
            
        } catch (Exception e) {
            log.error("秒杀处理异常", e);
            // 异常情况下恢复库存
            redisService.increment(stockKey, 1);
            res.setStatus(2);
            res.setMessage("秒杀失败，请稍后重试");
        }
        
        return res;
    }
}
