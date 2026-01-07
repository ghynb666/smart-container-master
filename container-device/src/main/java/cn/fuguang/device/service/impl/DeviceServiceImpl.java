package cn.fuguang.device.service.impl;

import cn.fuguang.api.device.req.CreateOrderOpenGateReqDTO;
import cn.fuguang.common.redis.service.RedisService;
import cn.fuguang.constants.MonitorConstants;
import cn.fuguang.constants.RedisConstants;
import cn.fuguang.device.mapper.DeviceInfoMapper;
import cn.fuguang.device.mapper.GateInfoMapper;
import cn.fuguang.device.pojo.bo.DeviceInfoBo;
import cn.fuguang.device.service.DeviceService;
import cn.fuguang.entity.DeviceInfoEntity;
import cn.fuguang.entity.GateInfoEntity;
import cn.fuguang.enums.DeviceStatusEnum;
import cn.fuguang.enums.GateStatusEnum;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.pojo.bean.DeviceWeightBean;
import cn.fuguang.pojo.bean.OrderOpenGateBean;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    @Resource
    private DeviceInfoMapper deviceInfoMapper;

    @Resource
    private GateInfoMapper gateInfoMapper;

    @Resource
    private RedisService redisService;


    @Override
    public void checkDeviceStatus(String deviceId, String gateId) {
        DeviceInfoEntity deviceInfoEntity = deviceInfoMapper.queryDeviceInfoById(deviceId);

        if (deviceInfoEntity == null){
            throw ContainerException.DATE_NOT_EXIST_ERROR.newInstance("设备不存在");
        }

        if (DeviceStatusEnum.NORMAL.name().equals(deviceInfoEntity.getDeviceStatus())){
            throw ContainerException.DEVICE_STATUS_ERROR;
        }

        GateInfoEntity gateInfoEntity = gateInfoMapper.queryGateInfoById(gateId);

        if (gateInfoEntity == null){
            throw ContainerException.DATE_NOT_EXIST_ERROR.newInstance("仓门不存在");
        }

        if (GateStatusEnum.NORMAL.name().equals(deviceInfoEntity.getDeviceStatus())){
            throw ContainerException.DEVICE_STATUS_ERROR.newInstance("仓门状态异常");
        }

        // TODO: 2024/5/11  校验仓门货道商品信息

    }

    @Override
    public void doCreateOpenGateOrderCache(CreateOrderOpenGateReqDTO reqDTO) {
        //开门前重量缓存
        String deviceWeightJson = redisService.getKey(RedisConstants.DEVICE_WEIGHT_INFO + reqDTO.getDeviceId() + "_" + reqDTO.getGateId());
        if (deviceWeightJson == null){
            log.error("订单开门设备重量未找到 reqDTO:{}", JSONObject.toJSONString(reqDTO));
            throw ContainerException.DEVICE_CACHE_ERROR.newInstance(MonitorConstants.DEVICE_WEIGHT_CACHE_ERROR + "设备重量缓存缺失");
        }
        DeviceWeightBean deviceWeightBean;
        try {
            deviceWeightBean = JSONObject.parseObject(deviceWeightJson, DeviceWeightBean.class);
        } catch (Exception e) {
            log.error("订单开门设备重量json转化异常 reqDTO:{}", JSONObject.toJSONString(reqDTO));
            throw ContainerException.DEVICE_CACHE_ERROR.newInstance(MonitorConstants.DEVICE_WEIGHT_CACHE_ERROR + "设备重量缓存json转化异常");
        }

        OrderOpenGateBean orderOpenGateBean = new OrderOpenGateBean();
        orderOpenGateBean.setOrderNo(reqDTO.getOrderNo());
        orderOpenGateBean.setDeviceId(reqDTO.getDeviceId());
        orderOpenGateBean.setGateId(reqDTO.getGateId());
        orderOpenGateBean.setCreateTime(new Date());
        orderOpenGateBean.setWeightMap(deviceWeightBean.getWeightMap());
        redisService.set(RedisConstants.ORDER_OPEN_GATE + reqDTO.getOrderNo(), JSONObject.toJSONString(orderOpenGateBean));
    }

    @Override
    public List<DeviceInfoEntity> queryByBo(DeviceInfoBo deviceInfoBo) {
        return deviceInfoMapper.queryByBo(deviceInfoBo);
    }

    @Override
    public void updateDeviceStatus(String deviceId, String status, String deviceStatus) {
        DeviceInfoEntity updateEntity = new DeviceInfoEntity();
        updateEntity.setDeviceId(deviceId);
        updateEntity.setDeviceStatus(status);
        updateEntity.setOldDeviceStatus(deviceStatus);

        int result = deviceInfoMapper.updateDeviceStatus(updateEntity);

        if (result != 1){
            throw ContainerException.DATABASE_UPDATE_ERROR.newInstance("变更设备状态更新数据库异常");
        }

    }

    @Override
    public DeviceInfoEntity queryByDeviceSn(String deviceSn) {
        DeviceInfoEntity deviceInfoEntity = deviceInfoMapper.queryByDeviceSn(deviceSn);
        if (deviceInfoEntity == null){
            throw ContainerException.DATE_NOT_EXIST_ERROR.newInstance("设备不存在 deviceSn:" + deviceSn);
        }
        return deviceInfoEntity;
    }
}
