package cn.fuguang.device.processor.deviceEvent;

import cn.fuguang.common.redis.service.RedisService;
import cn.fuguang.constants.RedisConstants;
import cn.fuguang.device.service.DeviceService;
import cn.fuguang.entity.DeviceInfoEntity;
import cn.fuguang.enums.DeviceStatusEnum;
import cn.fuguang.enums.EventTypeEnum;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.pojo.event.HeartEventBean;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 心跳事件处理器
 */
@Service
@Slf4j
public class DeviceHeartEventProcessor extends DeviceEventAbstractProcessor {


    @Resource
    private DeviceService deviceService;

    @Resource
    private RedisService redisService;

    @Override
    public String getProcessorType() {
        return EventTypeEnum.HEART.name();
    }

    @Override
    public void doDeviceEvent(JSONObject params) {


        HeartEventBean heartEventBean;
        try {
            heartEventBean = JSONObject.parseObject(JSONObject.toJSONString(params), HeartEventBean.class);
        } catch (Exception e) {
            log.error("处理心跳json转化异常 params:" + params, e);
            throw ContainerException.JSON_CONVERT_ERROR.newInstance("处理心跳json转化异常");
        }

        //判断设备是否在线
        DeviceInfoEntity deviceInfoEntity = deviceService.queryByDeviceSn(heartEventBean.getDeviceSn());

        //如果设备目前是下线状态 变更状态
        if (DeviceStatusEnum.OFF_LINE.name().equals(deviceInfoEntity.getDeviceStatus())){
            deviceService.updateDeviceStatus(deviceInfoEntity.getDeviceId(), DeviceStatusEnum.NORMAL.name(), DeviceStatusEnum.OFF_LINE.name());
        }

        //设置最后一次心跳缓存
        redisService.hset(RedisConstants.DEVICE_HEART_LAST_TIME_KEY, heartEventBean.getDeviceSn(), heartEventBean.getHeartDate());

        //获取最近5分钟的开始时间  例 目前是 2024-05-24 21:57:00  最近5分钟开始时间为2024-05-24 21:55:00
        //获取最近5分钟的结束时间  例 目前是 2024-05-24 21:57:00  最近5分钟结束时间为2024-05-24 21:59:59
        String startTimeKey = "";
        String endTimeKey = "";

        String key = "SERVICE_HEART_COUNT_KEY_" + "2024-05-24 21:55:00" + "_" +"2024-05-24 21:59:59";

        //获取当前时间所对应的redisKey
        String heartCountRedisKey = RedisConstants.SERVICE_HEART_COUNT_KEY + startTimeKey + "_" + endTimeKey;

        //增加心跳计数器的值
        redisService.increment(heartCountRedisKey, 1);
    }
}
