package cn.fuguang.device.biz.impl;

import cn.fuguang.api.device.req.CheckDeviceStatusReqDTO;
import cn.fuguang.common.redis.service.RedisService;
import cn.fuguang.constants.BaseConstants;
import cn.fuguang.constants.MonitorConstants;
import cn.fuguang.constants.RedisConstants;
import cn.fuguang.device.biz.DeviceInfoBiz;
import cn.fuguang.device.pojo.bo.DeviceInfoBo;
import cn.fuguang.device.service.DeviceService;
import cn.fuguang.entity.DeviceInfoEntity;
import cn.fuguang.enums.DeviceStatusEnum;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class DeviceInfoBizImpl implements DeviceInfoBiz {

    @Resource
    private DeviceService deviceService;

    @Resource
    private RedisService redisService;

    @Override
    public void checkDeviceStatus(CheckDeviceStatusReqDTO reqDTO) {
        deviceService.checkDeviceStatus(reqDTO.getDeviceId(), reqDTO.getGateId());
    }

    @Override
    public void checkDeviceHeart() {

        //获取所有在线的设备
        List<DeviceInfoEntity> deviceInfoEntityList = deviceService.queryByBo(DeviceInfoBo.builder().status(DeviceStatusEnum.NORMAL.name()).build());

        Date curTime = new Date();

        int onLineDeviceSize = deviceInfoEntityList.size();
        log.info("目前在线设备数量为:{}", onLineDeviceSize);

        double heartThreshold = ((double) (BaseConstants.DEVICE_OFF_LINE_MAX_TIME_INTERVAL * 60) /BaseConstants.DEVICE_HEART_INTERVAL)
                * onLineDeviceSize * BaseConstants.DEVICE_HEART_COUNT_THRESHOLD;


        // TODO: 2024/5/25 获取时间的工具
        //获取上5分钟的开始时间  例 目前是 2024-05-24 21:57:00  最近5分钟开始时间为2024-05-24 21:50:00
        //获取上5分钟的结束时间  例 目前是 2024-05-24 21:57:00  最近5分钟开始时间为2024-05-24 21:54:59
        String startTimeKey = "";
        String endTimeKey = "";

        //获取当前时间所对应的redisKey
        String heartCountRedisKey = RedisConstants.SERVICE_HEART_COUNT_KEY + startTimeKey + "_" + endTimeKey;
        long count = Long.parseLong(redisService.getKey(heartCountRedisKey));


        //遍历在线设备查询目前的心跳时间
        for (DeviceInfoEntity deviceInfoEntity : deviceInfoEntityList) {
            Date lastHeartTime = (Date) redisService.hget(RedisConstants.DEVICE_HEART_LAST_TIME_KEY, deviceInfoEntity.getDeviceSn());

            long minuteBetween = DateUtil.between(lastHeartTime, curTime, DateUnit.MINUTE);

            if (minuteBetween <= BaseConstants.DEVICE_OFF_LINE_MAX_TIME_INTERVAL) {
                break;
            }

            if (count < heartThreshold) {
                log.error(MonitorConstants.DEVICE_HEART_SERVICE_NETWORK_ERROR + "目前服务网络问题,心跳次数异常 redisKey:{}, heartThreshold:{}, deviceId:{}", heartCountRedisKey, heartThreshold, deviceInfoEntity.getDeviceId());
                break;
            }

            log.error(MonitorConstants.DEVICE_HEART_GT_MAX_INTERVAL_ERROR + "设备:{}最后一次心跳时间大于5分钟,请及时处理,目前暂时下线处理", deviceInfoEntity.getDeviceId());


            try {
                deviceService.updateDeviceStatus(deviceInfoEntity.getDeviceId(), DeviceStatusEnum.OFF_LINE.name(), deviceInfoEntity.getDeviceStatus());
            } catch (Exception e) {
                log.error("设备下线变更数据库异常 deviceId:{}", deviceInfoEntity.getDeviceId());
            }
        }
    }
}
