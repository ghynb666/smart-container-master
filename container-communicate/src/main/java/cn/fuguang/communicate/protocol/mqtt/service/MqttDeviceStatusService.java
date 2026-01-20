package cn.fuguang.communicate.protocol.mqtt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * MQTT设备状态服务
 */
@Service
@Slf4j
public class MqttDeviceStatusService {
    
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    
    // Redis键前缀：设备在线状态
    private static final String DEVICE_ONLINE_PREFIX = "DEVICE_ONLINE_";
    
    // Redis键前缀：设备心跳时间
    private static final String DEVICE_HEARTBEAT_PREFIX = "DEVICE_HEARTBEAT_";
    
    // 设备在线状态过期时间（秒），300秒后自动过期，需要设备定时更新
    private static final long ONLINE_EXPIRE_TIME = 300;
    
    /**
     * 更新设备在线状态
     * @param deviceSn 设备SN
     * @param isOnline 是否在线
     */
    public void updateDeviceOnlineStatus(String deviceSn, boolean isOnline) {
        String key = DEVICE_ONLINE_PREFIX + deviceSn;
        if (isOnline) {
            // 设备在线，设置Redis键值对，并设置过期时间
            redisTemplate.opsForValue().set(key, "1", ONLINE_EXPIRE_TIME, TimeUnit.SECONDS);
            log.info("设备上线：设备SN={}", deviceSn);
        } else {
            // 设备离线，删除Redis键值对
            redisTemplate.delete(key);
            log.info("设备离线：设备SN={}", deviceSn);
        }
    }
    
    /**
     * 获取设备在线状态
     * @param deviceSn 设备SN
     * @return true-在线，false-离线
     */
    public boolean isDeviceOnline(String deviceSn) {
        String key = DEVICE_ONLINE_PREFIX + deviceSn;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * 更新设备心跳时间
     * @param deviceSn 设备SN
     */
    public void updateDeviceHeartbeat(String deviceSn) {
        String key = DEVICE_HEARTBEAT_PREFIX + deviceSn;
        long currentTime = System.currentTimeMillis();
        redisTemplate.opsForValue().set(key, String.valueOf(currentTime));
        
        // 同时更新设备在线状态
        updateDeviceOnlineStatus(deviceSn, true);
    }
    
    /**
     * 获取设备最后心跳时间
     * @param deviceSn 设备SN
     * @return 最后心跳时间（毫秒），返回0表示未找到
     */
    public long getDeviceLastHeartbeat(String deviceSn) {
        String key = DEVICE_HEARTBEAT_PREFIX + deviceSn;
        String heartbeatTimeStr = redisTemplate.opsForValue().get(key);
        if (heartbeatTimeStr != null) {
            try {
                return Long.parseLong(heartbeatTimeStr);
            } catch (NumberFormatException e) {
                log.error("解析设备心跳时间失败：设备SN={}, 心跳时间={}", deviceSn, heartbeatTimeStr);
            }
        }
        return 0;
    }
}