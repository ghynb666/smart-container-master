package cn.fuguang.communicate.protocol.mqtt.service;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * MQTT消息服务
 */
@Service
public class MqttMessageService {
    
    @Resource(name = "mqttOutputChannel")
    private MessageChannel mqttOutputChannel;
    
    /**
     * 向指定主题发送MQTT消息
     * @param topic 主题
     * @param payload 消息体
     * @param qos QoS级别
     * @return true-发送成功，false-发送失败
     */
    public boolean sendMessage(String topic, String payload, int qos) {
        return mqttOutputChannel.send(MessageBuilder
                .withPayload(payload)
                .setHeader("mqtt_topic", topic)
                .setHeader("mqtt_qos", qos)
                .build());
    }
    
    /**
     * 向指定主题发送MQTT消息，使用默认QoS级别
     * @param topic 主题
     * @param payload 消息体
     * @return true-发送成功，false-发送失败
     */
    public boolean sendMessage(String topic, String payload) {
        return sendMessage(topic, payload, 1); // 默认QoS=1
    }
    
    /**
     * 向设备发送命令
     * @param deviceSn 设备SN
     * @param command 命令内容
     * @param qos QoS级别
     * @return true-发送成功，false-发送失败
     */
    public boolean sendDeviceCommand(String deviceSn, String command, int qos) {
        // 设备命令主题格式：device/down/{设备SN}
        String topic = "device/down/" + deviceSn;
        return sendMessage(topic, command, qos);
    }
    
    /**
     * 向设备发送命令，使用默认QoS级别
     * @param deviceSn 设备SN
     * @param command 命令内容
     * @return true-发送成功，false-发送失败
     */
    public boolean sendDeviceCommand(String deviceSn, String command) {
        return sendDeviceCommand(deviceSn, command, 1); // 默认QoS=1
    }
}