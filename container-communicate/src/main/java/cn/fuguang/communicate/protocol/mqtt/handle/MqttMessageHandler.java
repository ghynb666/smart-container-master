package cn.fuguang.communicate.protocol.mqtt.handle;

import cn.fuguang.common.redis.service.RedisService;
import cn.fuguang.common.rocketmq.service.RocketMqService;
import cn.fuguang.communicate.protocol.mqtt.service.MqttDeviceStatusService;
import cn.fuguang.constants.RedisConstants;
import cn.fuguang.constants.RocketMQConstants;
import cn.fuguang.pojo.bean.WeightReportData;
import cn.fuguang.pojo.bean.WeightStorageMessage;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MQTT消息处理器
 */
@Component
@Slf4j
public class MqttMessageHandler implements MessageHandler {
    
    @Autowired
    private MqttDeviceStatusService deviceStatusService;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private RocketMqService rocketMqService;
    
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            // 获取主题
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            // 获取QoS级别
            Integer qos = (Integer) message.getHeaders().get("mqtt_receivedQos");
            // 获取消息体
            String payload = (String) message.getPayload();
            
            log.info("收到MQTT消息：主题={}, QoS={}, 消息体={}", topic, qos, payload);
            
            // 根据主题处理不同类型的消息
            if (topic.startsWith("device/up/")) {
                // 设备上报消息
                handleDeviceUpMessage(topic, qos, payload);
            } else if (topic.startsWith("server/up/")) {
                // 服务器上报消息
                handleServerUpMessage(topic, qos, payload);
            } else if (topic.startsWith("device/will/")) {
                // 设备遗嘱消息
                handleDeviceWillMessage(topic, qos, payload);
            }
        } catch (Exception e) {
            log.error("处理MQTT消息发生异常：{}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理设备上报消息
     * @param topic 主题
     * @param qos QoS级别
     * @param payload 消息体
     */
    private void handleDeviceUpMessage(String topic, Integer qos, String payload) {
        // 从主题中提取设备SN：device/up/{设备SN}
        String[] topicParts = topic.split("/");
        if (topicParts.length >= 3) {
            String deviceSn = topicParts[2];
            log.info("处理设备上报消息：设备SN={}, 主题={}, 消息体={}", deviceSn, topic, payload);
            
            // 更新设备在线状态
            deviceStatusService.updateDeviceOnlineStatus(deviceSn, true);
            
            // 解析消息体
            try {
                JSONObject messageJson = JSON.parseObject(payload);
                String messageType = messageJson.getString("messageType");
                
                // 根据消息类型处理不同的业务逻辑
                switch (messageType) {
                    case "heartbeat":
                        // 心跳消息
                        handleHeartbeatMessage(deviceSn, messageJson);
                        break;
                    case "deviceStatus":
                        // 设备状态上报
                        handleDeviceStatusMessage(deviceSn, messageJson);
                        break;
                    case "weightReport":
                        // 重量上报
                        handleWeightReportMessage(deviceSn, messageJson);
                        break;
                    case "temperatureReport":
                        // 温度上报
                        handleTemperatureReportMessage(deviceSn, messageJson);
                        break;
                    default:
                        log.warn("未知的消息类型：{}", messageType);
                        break;
                }
            } catch (Exception e) {
                log.error("解析设备上报消息失败：设备SN={}, 消息体={}, 异常={}", deviceSn, payload, e.getMessage(), e);
            }
        }
    }
    
    /**
     * 处理服务器上报消息
     * @param topic 主题
     * @param qos QoS级别
     * @param payload 消息体
     */
    private void handleServerUpMessage(String topic, Integer qos, String payload) {
        // 从主题中提取服务器IP：server/up/{服务器IP}
        String[] topicParts = topic.split("/");
        if (topicParts.length >= 3) {
            String serverIp = topicParts[2];
            log.info("处理服务器上报消息：服务器IP={}, 主题={}, 消息体={}", serverIp, topic, payload);
            
            // TODO: 实现服务器上报消息的具体处理逻辑
        }
    }
    
    /**
     * 处理设备遗嘱消息
     * @param topic 主题
     * @param qos QoS级别
     * @param payload 消息体
     */
    private void handleDeviceWillMessage(String topic, Integer qos, String payload) {
        // 从主题中提取设备SN：device/will/{设备SN}
        String[] topicParts = topic.split("/");
        if (topicParts.length >= 3) {
            String deviceSn = topicParts[2];
            log.info("处理设备遗嘱消息：设备SN={}, 主题={}, 消息体={}", deviceSn, topic, payload);
            
            // 更新设备状态为离线
            deviceStatusService.updateDeviceOnlineStatus(deviceSn, false);
        }
    }
    
    /**
     * 处理心跳消息
     * @param deviceSn 设备SN
     * @param messageJson 消息JSON对象
     */
    private void handleHeartbeatMessage(String deviceSn, JSONObject messageJson) {
        log.debug("收到设备心跳消息：设备SN={}", deviceSn);
        // TODO: 处理心跳消息，如更新设备心跳时间
    }
    
    /**
     * 处理设备状态上报消息
     * @param deviceSn 设备SN
     * @param messageJson 消息JSON对象
     */
    private void handleDeviceStatusMessage(String deviceSn, JSONObject messageJson) {
        log.info("收到设备状态上报消息：设备SN={}, 状态={}", deviceSn, messageJson.toJSONString());
        // TODO: 处理设备状态上报，如更新设备运行状态、电池电量等
    }
    
    /**
     * 处理重量上报消息
     * @param deviceSn 设备SN
     * @param messageJson 消息JSON对象
     */
    private void handleWeightReportMessage(String deviceSn, JSONObject messageJson) {
        log.info("收到重量上报消息：设备SN={}, 消息内容={}", deviceSn, messageJson.toJSONString());
        
        try {
            // 解析重量上报数据
            WeightReportData weightReportData = new WeightReportData();
            weightReportData.setDeviceSn(deviceSn);
            // 使用 fastjson2 正确的方法获取带默认值的属性
            Long timestamp = messageJson.getLong("timestamp");
            weightReportData.setTimestamp(timestamp != null ? timestamp : System.currentTimeMillis());
            
            String gateStatus = messageJson.getString("gateStatus");
            weightReportData.setGateStatus(gateStatus != null ? gateStatus : "0");
            
            // 解析重量信息，兼容两种格式：单重量值和多货道重量映射
            Map<Integer, BigDecimal> weightMap = new HashMap<>();
            if (messageJson.containsKey("weightMap")) {
                // 多货道重量映射格式
                JSONObject weightMapJson = messageJson.getJSONObject("weightMap");
                for (String laneIdStr : weightMapJson.keySet()) {
                    Integer laneId = Integer.parseInt(laneIdStr);
                    BigDecimal weight = weightMapJson.getBigDecimal(laneIdStr);
                    weightMap.put(laneId, weight);
                }
            } else {
                // 单重量值格式（默认货道1）
                BigDecimal weight = messageJson.getBigDecimal("weight");
                if (weight != null) {
                    weightMap.put(1, weight);
                }
            }
            weightReportData.setWeightMap(weightMap);
            
            // 1. 先缓存到Redis，使用Hash结构
            // 构建Redis Hash值
            Map<String, Object> redisHashValue = new HashMap<>();
            redisHashValue.put("gateStatus", weightReportData.getGateStatus());
            redisHashValue.put("weightMap", weightReportData.getWeightMap());
            redisHashValue.put("lastUpdateTime", weightReportData.getTimestamp());
            
            // 存储到Redis：大key为WEIGHT_INFO，小key为设备SN
            for (Map.Entry<String, Object> entry : redisHashValue.entrySet()) {
                redisService.hset(RedisConstants.WEIGHT_INFO, deviceSn + ":" + entry.getKey(), entry.getValue());
            }
            
            log.info("重量数据已缓存到Redis，设备SN={}", deviceSn);
            
            // 2. 发送MQ消息进行异步落库
            WeightStorageMessage storageMessage = new WeightStorageMessage();
            storageMessage.setMessageId(UUID.randomUUID().toString());
            storageMessage.setDeviceSn(deviceSn);
            storageMessage.setGateStatus(weightReportData.getGateStatus());
            storageMessage.setWeightMap(weightReportData.getWeightMap());
            storageMessage.setTimestamp(weightReportData.getTimestamp());
            storageMessage.setCreateTime(System.currentTimeMillis());
            
            // 发送消息到RocketMQ
            rocketMqService.sendMessage(RocketMQConstants.WEIGHT_DATA_STORAGE_TOPIC, JSONObject.toJSONString(storageMessage));
            
            log.info("重量数据落库消息已发送到MQ，设备SN={}, 消息ID={}", deviceSn, storageMessage.getMessageId());
            
        } catch (Exception e) {
            log.error("处理重量上报消息失败：设备SN={}, 消息={}, 异常={}", deviceSn, messageJson.toJSONString(), e.getMessage(), e);
        }
    }
    
    /**
     * 处理温度上报消息
     * @param deviceSn 设备SN
     * @param messageJson 消息JSON对象
     */
    private void handleTemperatureReportMessage(String deviceSn, JSONObject messageJson) {
        Double temperature = messageJson.getDouble("temperature");
        log.info("收到温度上报消息：设备SN={}, 温度={}", deviceSn, temperature);
        // TODO: 处理温度上报，如更新冷藏温度、检测异常等
    }
}