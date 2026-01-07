package cn.fuguang.device.listener.redis;

import cn.fuguang.constants.RedisConstants;
import cn.fuguang.device.processor.redisListener.HeartCountExpireProcessor;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class RedisKeyExpireListener extends KeyExpirationEventMessageListener {

    @Resource
    private HeartCountExpireProcessor heartCountExpireProcessor;

    public RedisKeyExpireListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("监听到redisKey过期事件 expiredKey:{}", JSONObject.toJSONString(expiredKey));

        if (expiredKey.startsWith(RedisConstants.SERVICE_HEART_COUNT_KEY)){
            heartCountExpireProcessor.handle(expiredKey);
        }

    }

}
