package cn.fuguang.communicate.biz.impl;

import cn.fuguang.api.communicate.dto.req.EventAcceptReqDTO;
import cn.fuguang.communicate.biz.EventAcceptBiz;
import cn.fuguang.communicate.protocol.mqtt.service.MqttMessageService;
import cn.fuguang.communicate.protocol.netty.constant.CommandIdConstants;
import cn.fuguang.communicate.protocol.netty.entity.CustomProtocol;
import cn.fuguang.communicate.protocol.netty.manager.ConnectionManager;
import cn.fuguang.enums.CommunicateProtocolEnum;
import cn.fuguang.enums.EventTypeEnum;
import cn.fuguang.utils.ValidationUtils;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventAcceptBizImpl implements EventAcceptBiz {

    @Autowired
    private ConnectionManager connectionManager;
    
    @Autowired
    private MqttMessageService mqttMessageService;
    
    @Override
    public void eventAccept(EventAcceptReqDTO reqDTO) {
        ValidationUtils.validate(reqDTO);
        
        log.info("处理事件接收请求：事件类型={}, 通信协议={}, 设备厂商={}, 参数={}", 
                reqDTO.getEvent(), reqDTO.getProtocolEnum(), reqDTO.getDeviceSupplierEnum(), reqDTO.getParams());
        
        // 获取设备SN
        String deviceSn = (String) reqDTO.getParams().get("deviceSn");
        if (deviceSn == null || deviceSn.isEmpty()) {
            log.error("设备SN不能为空");
            return;
        }
        
        // 根据通信协议处理不同的事件
        switch (reqDTO.getProtocolEnum()) {
            case NETTY:
                // Netty TCP通信
                handleNettyEvent(reqDTO.getEvent(), deviceSn, reqDTO.getParams());
                break;
            case MQTT:
                // MQTT通信
                handleMqttEvent(reqDTO.getEvent(), deviceSn, reqDTO.getParams());
                break;
            default:
                log.error("不支持的通信协议：{}", reqDTO.getProtocolEnum());
                break;
        }
    }
    
    /**
     * 处理Netty TCP事件
     * @param eventType 事件类型
     * @param deviceSn 设备SN
     * @param params 参数
     */
    private void handleNettyEvent(EventTypeEnum eventType, String deviceSn, java.util.Map<String, Object> params) {
        log.info("处理Netty事件：事件类型={}, 设备SN={}", eventType, deviceSn);
        
        // 根据事件类型创建相应的命令
        CustomProtocol protocol = new CustomProtocol();
        protocol.setVersion((byte) 0x01); // 默认版本号
        protocol.setDeviceSn(deviceSn);
        
        switch (eventType) {
            case HEART:
                // 心跳事件
                protocol.setCommandId(CommandIdConstants.HEARTBEAT);
                protocol.setData(new byte[0]);
                break;
            case CLOSE_GATE:
                // 关门事件
                protocol.setCommandId(CommandIdConstants.CLOSE_GATE);
                // 获取柜门编号
                Integer gateNumber = (Integer) params.get("gateNumber");
                if (gateNumber != null) {
                    protocol.setData(new byte[]{gateNumber.byteValue()});
                } else {
                    protocol.setData(new byte[0]);
                }
                break;
            default:
                log.error("不支持的Netty事件类型：{}", eventType);
                return;
        }
        
        // 向设备发送命令
        boolean success = connectionManager.sendMessage(deviceSn, protocol);
        if (success) {
            log.info("向设备发送Netty命令成功：设备SN={}, 事件类型={}", deviceSn, eventType);
        } else {
            log.error("向设备发送Netty命令失败：设备SN={}, 事件类型={}", deviceSn, eventType);
        }
    }
    
    /**
     * 处理MQTT事件
     * @param eventType 事件类型
     * @param deviceSn 设备SN
     * @param params 参数
     */
    private void handleMqttEvent(EventTypeEnum eventType, String deviceSn, java.util.Map<String, Object> params) {
        log.info("处理MQTT事件：事件类型={}, 设备SN={}", eventType, deviceSn);
        
        // 构建MQTT消息体
        JSONObject messageJson = new JSONObject();
        messageJson.put("messageType", eventType.name().toLowerCase());
        messageJson.putAll(params);
        
        // 构建主题：device/down/{设备SN}
        String topic = "device/down/" + deviceSn;
        
        // 发送MQTT消息
        boolean success = mqttMessageService.sendMessage(topic, messageJson.toJSONString());
        if (success) {
            log.info("向设备发送MQTT命令成功：设备SN={}, 事件类型={}", deviceSn, eventType);
        } else {
            log.error("向设备发送MQTT命令失败：设备SN={}, 事件类型={}", deviceSn, eventType);
        }
    }
}
