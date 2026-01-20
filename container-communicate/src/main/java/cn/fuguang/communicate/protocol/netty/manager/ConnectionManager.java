package cn.fuguang.communicate.protocol.netty.manager;

import cn.fuguang.communicate.protocol.netty.entity.CustomProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Netty TCP连接管理器
 */
@Component
@Slf4j
public class ConnectionManager {
    
    // 设备SN到Channel的映射
    private final Map<String, Channel> deviceChannelMap = new ConcurrentHashMap<>();
    
    // Channel到设备SN的映射
    private final Map<Channel, String> channelDeviceMap = new ConcurrentHashMap<>();
    
    // Channel属性：设备SN
    public static final AttributeKey<String> DEVICE_SN_ATTRIBUTE = AttributeKey.newInstance("deviceSn");
    
    /**
     * 建立设备连接
     * @param deviceSn 设备SN
     * @param channel Channel对象
     */
    public void establishConnection(String deviceSn, Channel channel) {
        // 如果设备已存在连接，先断开旧连接
        Channel oldChannel = deviceChannelMap.get(deviceSn);
        if (oldChannel != null && oldChannel != channel) {
            disconnect(oldChannel);
        }
        
        // 建立新连接映射
        deviceChannelMap.put(deviceSn, channel);
        channelDeviceMap.put(channel, deviceSn);
        
        // 设置Channel属性
        channel.attr(DEVICE_SN_ATTRIBUTE).set(deviceSn);
        
        log.info("设备连接建立：设备SN={}, Channel={}", deviceSn, channel.id());
    }
    
    /**
     * 断开设备连接
     * @param channel Channel对象
     */
    public void disconnect(Channel channel) {
        String deviceSn = channelDeviceMap.remove(channel);
        if (deviceSn != null) {
            deviceChannelMap.remove(deviceSn);
            log.info("设备连接断开：设备SN={}, Channel={}", deviceSn, channel.id());
        }
        
        // 关闭Channel
        if (channel.isActive()) {
            channel.close();
        }
    }
    
    /**
     * 获取设备对应的Channel
     * @param deviceSn 设备SN
     * @return Channel对象，不存在则返回null
     */
    public Channel getChannelByDeviceSn(String deviceSn) {
        return deviceChannelMap.get(deviceSn);
    }
    
    /**
     * 获取Channel对应的设备SN
     * @param channel Channel对象
     * @return 设备SN，不存在则返回null
     */
    public String getDeviceSnByChannel(Channel channel) {
        return channelDeviceMap.get(channel);
    }
    
    /**
     * 向设备发送消息
     * @param deviceSn 设备SN
     * @param protocol 协议对象
     * @return true-发送成功，false-发送失败
     */
    public boolean sendMessage(String deviceSn, CustomProtocol protocol) {
        Channel channel = getChannelByDeviceSn(deviceSn);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(protocol);
            log.debug("向设备发送消息成功：设备SN={}, 命令ID={}", deviceSn, protocol.getCommandId());
            return true;
        } else {
            log.warn("向设备发送消息失败，设备不在线：设备SN={}", deviceSn);
            return false;
        }
    }
    
    /**
     * 获取当前连接设备数量
     * @return 连接设备数量
     */
    public int getConnectionCount() {
        return deviceChannelMap.size();
    }
    
    /**
     * 清理所有连接
     */
    public void clearAllConnections() {
        for (Channel channel : channelDeviceMap.keySet()) {
            if (channel.isActive()) {
                channel.close();
            }
        }
        deviceChannelMap.clear();
        channelDeviceMap.clear();
        log.info("所有连接已清理");
    }
}