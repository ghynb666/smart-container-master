package cn.fuguang.common.rocketmq.service;

import cn.fuguang.constants.BaseConstants;
import cn.fuguang.exception.ContainerException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class RocketMqService {

    @Resource
    private RocketMQTemplate rocketMQTemplate;


    public void sendMessage(final String topic, final String msg) {
        try{
            rocketMQTemplate.asyncSend(topic, msg,new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("rocketMQ发送异步消息成功,消息体:{}, sendResult={}",msg, sendResult);
                }

                @Override
                public void onException(Throwable t) {
                    log.error("rocketMQ发送异步消息异常, 转同步，消息体："+msg, t);
                    SendResult sendResult =rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msg).build());
                    log.info("rocketMQ发送同步消息，消息体："+msg+" ,sendResult :"+ JSONObject.toJSONString(sendResult));
                }
            });
        }catch (Throwable t){
            log.error("rocketMQ发送消息异常,消息体:"+msg, t);
        }
    }

    /**
     * 发送同步消息
     */
    public void sendSyncMessage(String topic, String msg){
        log.info("rocketMQ开始发送同步消息,topic:{},消息体:{}",topic,msg);
        SendResult sendResult =rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msg).build());
        log.info("rocketMQ发送同步消息,消息体:"+msg+",sendResult:"+JSONObject.toJSONString(sendResult));
        if(!SendStatus.SEND_OK.equals(sendResult.getSendStatus())){
            throw ContainerException.ROCKETMQ_SEND_ERROR.newInstance("rocketMQ发送同步消息失败,msg:" + msg);
        }
    }

    public void sendDelayMessage(String topic, String msg, int delayLevel){
        try{
            log.info("rocketMQ开始发送延迟消息,topic:{},消息体:{},delayLevel:{}",topic,msg,delayLevel);
            SendResult sendResult =rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msg).build(), BaseConstants.ROCKETMQ_TIMEOUT_MILES, delayLevel);
            log.info("rocketMQ发送延迟消息,消息体:"+msg+",sendResult:"+JSONObject.toJSONString(sendResult));
            if(!SendStatus.SEND_OK.equals(sendResult.getSendStatus())){
                throw ContainerException.ROCKETMQ_SEND_ERROR.newInstance("rocketMQ发送延迟消息失败,msg:" + msg);
            }
        }catch (Throwable t){
            log.error("rocketMQ发送延迟消息异常,消息体:"+msg, t);
            throw ContainerException.ROCKETMQ_SEND_ERROR.newInstance("rocketMQ发送延迟消息异常,msg:" + msg);
        }
    }

    public void sendOrderlyMessage(String topic, String msg, String hash){
        try{
            log.info("rocketMQ开始发送顺序消息,topic:{},消息体:{},hash:{}",topic,msg,hash);
            SendResult sendResult =rocketMQTemplate.syncSendOrderly(topic, MessageBuilder.withPayload(msg).build(), hash);
            log.info("rocketMQ发送顺序消息,消息体:"+msg+",sendResult:"+JSONObject.toJSONString(sendResult));
            if(!SendStatus.SEND_OK.equals(sendResult.getSendStatus())){
                throw ContainerException.ROCKETMQ_SEND_ERROR.newInstance("rocketMQ发送顺序消息失败,msg:" + msg);
            }
        }catch (Throwable t){
            log.error("rocketMQ发送顺序消息异常,消息体:"+msg, t);
        }
    }

}
