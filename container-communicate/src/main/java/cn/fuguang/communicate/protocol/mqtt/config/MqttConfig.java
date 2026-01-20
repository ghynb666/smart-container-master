package cn.fuguang.communicate.protocol.mqtt.config;

import cn.fuguang.communicate.protocol.mqtt.handle.MqttMessageHandler;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * MQTT配置类
 */
@Configuration
public class MqttConfig {
    
    @Autowired
    private MqttProperties mqttProperties;
    
    @Autowired
    private MqttMessageHandler mqttMessageHandler;
    
    /**
     * MQTT客户端工厂
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        
        // 设置MQTT服务器地址
        options.setServerURIs(new String[] { mqttProperties.getBroker() });
        
        // 客户端ID在创建MqttClient时设置，不需要在这里设置
        
        // 设置用户名和密码
        if (mqttProperties.getUsername() != null && !mqttProperties.getUsername().isEmpty()) {
            options.setUserName(mqttProperties.getUsername());
        }
        if (mqttProperties.getPassword() != null && !mqttProperties.getPassword().isEmpty()) {
            options.setPassword(mqttProperties.getPassword().toCharArray());
        }
        
        // 设置连接超时时间
        options.setConnectionTimeout(mqttProperties.getConnectionTimeout());
        
        // 设置会话保持时间
        options.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());
        
        // 设置是否清除会话
        options.setCleanSession(mqttProperties.isCleanSession());
        
        // 设置是否自动重连
        options.setAutomaticReconnect(mqttProperties.isAutomaticReconnect());
        
        factory.setConnectionOptions(options);
        return factory;
    }
    
    /**
     * MQTT消息输入通道
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }
    
    /**
     * MQTT消息输出通道
     */
    @Bean
    public MessageChannel mqttOutputChannel() {
        return new DirectChannel();
    }
    
    /**
     * MQTT消息生产者
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutputChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = 
                new MqttPahoMessageHandler(mqttProperties.getClientId() + "_producer", mqttClientFactory());
        
        // 设置默认QoS级别
        messageHandler.setDefaultQos(mqttProperties.getDefaultQos());
        
        // 设置是否异步发送
        messageHandler.setAsync(true);
        
        // 设置异步发送的回调
        messageHandler.setAsyncEvents(true);
        
        // 设置消息转换器
        messageHandler.setConverter(new DefaultPahoMessageConverter());
        
        return messageHandler;
    }
    
    /**
     * MQTT消息消费者
     */
    @Bean
    public MessageProducer mqttInbound() {
        // 订阅的主题：device/up/# 表示接收所有设备上报的消息，device/will/# 表示接收设备遗嘱消息
        String[] topics = { "device/up/#", "server/up/#", "device/will/#" };
        
        MqttPahoMessageDrivenChannelAdapter adapter = 
                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getClientId() + "_consumer", 
                        mqttClientFactory(), topics);
        
        // 设置消息转换器
        adapter.setConverter(new DefaultPahoMessageConverter());
        
        // 设置默认QoS级别
        adapter.setQos(mqttProperties.getDefaultQos());
        
        // 设置消息通道
        adapter.setOutputChannel(mqttInputChannel());
        
        return adapter;
    }
    
    /**
     * MQTT消息处理器，处理接收到的MQTT消息
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {
        return mqttMessageHandler;
    }
}