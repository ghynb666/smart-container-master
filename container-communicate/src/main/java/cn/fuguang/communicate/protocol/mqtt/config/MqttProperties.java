package cn.fuguang.communicate.protocol.mqtt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MQTT配置属性类
 */
@Component
@ConfigurationProperties(prefix = "mqtt")
@Data
public class MqttProperties {
    
    // MQTT服务器地址，格式：tcp://host:port
    private String broker;
    
    // 客户端ID，用于标识MQTT客户端
    private String clientId;
    
    // 用户名
    private String username;
    
    // 密码
    private String password;
    
    // 连接超时时间（秒）
    private int connectionTimeout = 30;
    
    // 会话保持时间（秒）
    private int keepAliveInterval = 60;
    
    // 是否清除会话，false表示保持会话
    private boolean cleanSession = false;
    
    // 默认QoS级别
    private int defaultQos = 1;
    
    // 是否自动重连
    private boolean automaticReconnect = true;
}