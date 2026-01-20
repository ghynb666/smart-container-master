package cn.fuguang.order.listener.mq;

import cn.fuguang.constants.RocketMQConstants;
import cn.fuguang.order.service.SeckillService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@RocketMQMessageListener(topic = RocketMQConstants.SECKILL_COUPON_TOPIC, consumerGroup = RocketMQConstants.SECKILL_COUPON_CONSUMER)
public class SeckillConsumer implements RocketMQListener<String> {

    @Resource
    private SeckillService seckillService;

    @Override
    public void onMessage(String message) {
        log.info("收到秒杀成功消息: {}", message);
        
        try {
            // 解析消息体
            JSONObject msgBody = JSONObject.parseObject(message);
            String orderNo = msgBody.getString("orderNo");
            String customerId = msgBody.getString("customerId");
            String couponConfigId = msgBody.getString("couponConfigId");
            
            if (orderNo == null || customerId == null || couponConfigId == null) {
                log.error("秒杀消息参数无效: {}", message);
                return;
            }
            
            // 处理秒杀成功后的优惠券发放
            seckillService.processSeckillSuccess(customerId, couponConfigId, orderNo);
            
        } catch (Exception e) {
            log.error("处理秒杀消息异常: {}", message, e);
        }
    }
}
