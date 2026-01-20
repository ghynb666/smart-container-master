package cn.fuguang.device.listener.mq;

import cn.fuguang.constants.RocketMQConstants;
import cn.fuguang.device.service.WeightRecordService;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.pojo.bean.WeightStorageMessage;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 重量数据存储MQ消费者
 */
@Slf4j
@Service
@RocketMQMessageListener(
        consumerGroup = RocketMQConstants.WEIGHT_DATA_STORAGE_CONSUMER,
        topic = RocketMQConstants.WEIGHT_DATA_STORAGE_TOPIC,
        consumeThreadNumber = 10
)
public class WeightDataStorageConsumer implements RocketMQListener<MessageExt> {
    
    @Resource
    private WeightRecordService weightRecordService;
    
    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        String data = new String(body);
        log.info("重量数据存储消费者接收到消息，内容：{}", data);
        
        try {
            // 解析消息内容
            WeightStorageMessage storageMessage = JSONObject.parseObject(data, WeightStorageMessage.class);
            if (storageMessage == null || storageMessage.getMessageId() == null) {
                log.error("重量数据存储消息格式无效：{}", data);
                return;
            }
            
            // 调用重量记录服务处理数据存储
            weightRecordService.processWeightStorage(storageMessage);
            
        } catch (Exception e) {
            log.error("重量数据存储消费者消费异常，消息内容：{}", data, e);
            throw ContainerException.ROCKETMQ_CONSUMER_ERROR;
        }
    }
}