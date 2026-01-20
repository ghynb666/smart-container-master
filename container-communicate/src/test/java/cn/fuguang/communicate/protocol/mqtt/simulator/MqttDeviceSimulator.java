package cn.fuguang.communicate.protocol.mqtt.simulator;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * MQTT设备模拟器
 */
@Slf4j
public class MqttDeviceSimulator {
    
    // MQTT Broker地址
    private String brokerUrl;
    // 设备SN，用作Client ID
    private String deviceSn;
    // 用户名
    private String username;
    // 密码
    private String password;
    
    // MQTT客户端
    private MqttClient mqttClient;
    // 连接选项
    private MqttConnectOptions connectOptions;
    
    /**
     * 构造函数
     * @param brokerUrl MQTT Broker地址
     * @param deviceSn 设备SN
     * @param username 用户名
     * @param password 密码
     */
    public MqttDeviceSimulator(String brokerUrl, String deviceSn, String username, String password) {
        this.brokerUrl = brokerUrl;
        this.deviceSn = deviceSn;
        this.username = username;
        this.password = password;
    }
    
    /**
     * 启动设备模拟器
     */
    public void start() {
        try {
            // 创建MQTT客户端
            mqttClient = new MqttClient(brokerUrl, deviceSn, new MemoryPersistence());
            
            // 配置连接选项
            connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(false); // 保持会话
            
            // 设置用户名和密码
            if (username != null && !username.isEmpty()) {
                connectOptions.setUserName(username);
            }
            if (password != null && !password.isEmpty()) {
                connectOptions.setPassword(password.toCharArray());
            }
            
            // 设置连接超时时间
            connectOptions.setConnectionTimeout(30);
            
            // 设置会话保持时间
            connectOptions.setKeepAliveInterval(60);
            
            // 设置自动重连
            connectOptions.setAutomaticReconnect(true);
            
            // 设置遗嘱消息
            String willTopic = "device/will/" + deviceSn;
            String willMessage = "{\"messageType\":\"offline\",\"deviceSn\":\"" + deviceSn + "\"}";
            connectOptions.setWill(willTopic, willMessage.getBytes(), 1, false);
            
            // 设置回调函数
            mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    log.info("设备连接成功：设备SN={}, 是否重连={}, 服务器URI={}", deviceSn, reconnect, serverURI);
                    
                    // 订阅设备命令主题
                    subscribeToCommandTopic();
                    
                    // 发送上线消息
                    sendOnlineMessage();
                    
                    // 启动心跳线程
                    startHeartbeatThread();
                }
                
                @Override
                public void connectionLost(Throwable cause) {
                    log.warn("设备连接丢失：设备SN={}, 原因={}", deviceSn, cause.getMessage());
                }
                
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // 处理接收到的消息
                    String payload = new String(message.getPayload());
                    log.info("收到MQTT消息：设备SN={}, 主题={}, QoS={}, 消息体={}", 
                            deviceSn, topic, message.getQos(), payload);
                    
                    // 处理服务器下发的命令
                    handleServerCommand(topic, message);
                }
                
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // 消息发送完成回调
                    log.debug("消息发送完成：设备SN={}, 消息ID={}", deviceSn, token.getMessageId());
                }
            });
            
            // 连接到MQTT Broker
            mqttClient.connect(connectOptions);
            log.info("设备模拟器启动成功：设备SN={}", deviceSn);
        } catch (MqttException e) {
            log.error("设备模拟器启动失败：设备SN={}, 异常信息={}", deviceSn, e.getMessage(), e);
        }
    }
    
    /**
     * 订阅设备命令主题
     */
    private void subscribeToCommandTopic() {
        try {
            // 订阅主题：device/down/{设备SN}
            String commandTopic = "device/down/" + deviceSn;
            mqttClient.subscribe(commandTopic, 1); // QoS=1
            log.info("订阅设备命令主题成功：设备SN={}, 主题={}", deviceSn, commandTopic);
        } catch (MqttException e) {
            log.error("订阅设备命令主题失败：设备SN={}, 异常信息={}", deviceSn, e.getMessage(), e);
        }
    }
    
    /**
     * 发送上线消息
     */
    private void sendOnlineMessage() {
        String topic = "device/up/" + deviceSn;
        String message = "{\"messageType\":\"online\",\"deviceSn\":\"" + deviceSn + "\"}";
        sendMessage(topic, message, 1);
        log.info("发送上线消息：设备SN={}", deviceSn);
    }
    
    /**
     * 启动心跳线程，定时发送心跳包
     */
    private void startHeartbeatThread() {
        new Thread(() -> {
            while (true) {
                try {
                    // 每60秒发送一次心跳包
                    TimeUnit.SECONDS.sleep(60);
                    
                    // 发送心跳包
                    sendHeartbeat();
                } catch (InterruptedException e) {
                    log.error("心跳线程异常：设备SN={}, 异常信息={}", deviceSn, e.getMessage(), e);
                    break;
                }
            }
        }, "heartbeat-thread").start();
    }
    
    /**
     * 发送心跳包
     */
    public void sendHeartbeat() {
        String topic = "device/up/" + deviceSn;
        String message = "{\"messageType\":\"heartbeat\",\"deviceSn\":\"" + deviceSn + "\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}";
        sendMessage(topic, message, 0); // 心跳包使用QoS=0
        log.info("发送心跳包：设备SN={}", deviceSn);
    }
    
    /**
     * 发送设备状态上报
     */
    public void sendDeviceStatus() {
        String topic = "device/up/" + deviceSn;
        String message = "{\"messageType\":\"deviceStatus\",\"deviceSn\":\"" + deviceSn + "\",\"timestamp\":\"" + System.currentTimeMillis() + "\",\"battery\":95,\"temperature\":25.5,\"status\":\"normal\"}";
        sendMessage(topic, message, 1);
        log.info("发送设备状态上报：设备SN={}", deviceSn);
    }
    
    /**
     * 发送重量上报
     * @param weight 重量值
     */
    public void sendWeightReport(double weight) {
        String topic = "device/up/" + deviceSn;
        String message = "{\"messageType\":\"weightReport\",\"deviceSn\":\"" + deviceSn + "\",\"timestamp\":\"" + System.currentTimeMillis() + "\",\"weight\":\"" + weight + "\"}";
        sendMessage(topic, message, 1);
        log.info("发送重量上报：设备SN={}, 重量={}", deviceSn, weight);
    }
    
    /**
     * 发送MQTT消息
     * @param topic 主题
     * @param payload 消息体
     * @param qos QoS级别
     */
    public void sendMessage(String topic, String payload, int qos) {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(qos);
                mqttClient.publish(topic, message);
                log.debug("发送MQTT消息：设备SN={}, 主题={}, QoS={}, 消息体={}", deviceSn, topic, qos, payload);
            } else {
                log.error("设备未连接，无法发送消息：设备SN={}", deviceSn);
            }
        } catch (MqttException e) {
            log.error("发送MQTT消息失败：设备SN={}, 主题={}, 异常信息={}", deviceSn, topic, e.getMessage(), e);
        }
    }
    
    /**
     * 处理服务器下发的命令
     * @param topic 主题
     * @param message 消息
     */
    private void handleServerCommand(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            
            // TODO: 解析命令内容，执行相应的操作
            // 例如：开柜命令、关柜命令等
            log.info("处理服务器命令：设备SN={}, 命令内容={}", deviceSn, payload);
            
            // 示例：模拟执行开柜操作
            if (payload.contains("openGate")) {
                log.info("执行开柜操作：设备SN={}", deviceSn);
                // 发送开柜结果
                sendGateOperationResult("openGate", "success");
            }
        } catch (Exception e) {
            log.error("处理服务器命令失败：设备SN={}, 异常信息={}", deviceSn, e.getMessage(), e);
        }
    }
    
    /**
     * 发送柜门操作结果
     * @param operation 操作类型
     * @param result 操作结果
     */
    private void sendGateOperationResult(String operation, String result) {
        String topic = "device/up/" + deviceSn;
        String message = "{\"messageType\":\"gateOperationResult\",\"deviceSn\":\"" + deviceSn + "\",\"operation\":\"" + operation + "\",\"result\":\"" + result + "\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}";
        sendMessage(topic, message, 1);
        log.info("发送柜门操作结果：设备SN={}, 操作={}, 结果={}", deviceSn, operation, result);
    }
    
    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                log.info("设备断开连接：设备SN={}", deviceSn);
            }
        } catch (MqttException e) {
            log.error("设备断开连接失败：设备SN={}, 异常信息={}", deviceSn, e.getMessage(), e);
        }
    }
    
    /**
     * 主函数，用于启动MQTT设备模拟器
     * @param args 参数
     */
    public static void main(String[] args) {
        // 默认参数
        String brokerUrl = "tcp://127.0.0.1:1883";
        String deviceSn = "TEST_MQTT_DEVICE_001";
        String username = "";
        String password = "";
        
        // 解析命令行参数
        if (args.length >= 2) {
            brokerUrl = args[0];
            deviceSn = args[1];
        }
        if (args.length >= 4) {
            username = args[2];
            password = args[3];
        }
        
        // 创建并启动MQTT设备模拟器
        MqttDeviceSimulator simulator = new MqttDeviceSimulator(brokerUrl, deviceSn, username, password);
        simulator.start();
        
        // 控制台交互
        Scanner scanner = new Scanner(System.in);
        log.info("MQTT设备模拟器已启动，输入命令进行测试：");
        log.info("1. send-heartbeat - 发送心跳包");
        log.info("2. send-device-status - 发送设备状态上报");
        log.info("3. send-weight <weight> - 发送重量上报");
        log.info("4. disconnect - 断开连接");
        log.info("5. exit - 退出模拟器");
        
        while (true) {
            String input = scanner.nextLine();
            String[] parts = input.split(" ");
            
            if (parts[0].equals("send-heartbeat")) {
                // 发送心跳包
                simulator.sendHeartbeat();
            } else if (parts[0].equals("send-device-status")) {
                // 发送设备状态上报
                simulator.sendDeviceStatus();
            } else if (parts[0].equals("send-weight") && parts.length >= 2) {
                // 发送重量上报
                double weight = Double.parseDouble(parts[1]);
                simulator.sendWeightReport(weight);
            } else if (parts[0].equals("disconnect")) {
                // 断开连接
                simulator.disconnect();
            } else if (parts[0].equals("exit")) {
                // 退出模拟器
                simulator.disconnect();
                log.info("退出MQTT设备模拟器");
                System.exit(0);
            } else {
                log.info("无效命令，请重新输入");
            }
        }
    }
}