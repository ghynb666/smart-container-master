package cn.fuguang.device.listener.mq;

import cn.fuguang.constants.RocketMQConstants;
import cn.fuguang.device.biz.CommunicateEventBiz;
import cn.fuguang.exception.ContainerException;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
@RocketMQMessageListener(consumerGroup = RocketMQConstants.COMMUNICATE_EVENT_CONSUMER
        , topic = RocketMQConstants.COMMUNICATE_EVENT_TOPIC, consumeThreadNumber = 10)
public class CommunicateEventListener implements RocketMQListener<MessageExt> {


    @Resource
    private CommunicateEventBiz communicateEventBiz;

    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        String data = new String(body);
        try {
            JSONObject params = JSONObject.parseObject(data);
            log.info("communicate_event_consumer消费者接收到消息, params:{}", params);
            communicateEventBiz.doDeviceEvent(params);

        } catch (Exception e) {
            log.error("communicate_event_consumer消费者消费异常 params:" + data, e);
            throw ContainerException.ROCKETMQ_CONSUMER_ERROR;
        }
    }
}
