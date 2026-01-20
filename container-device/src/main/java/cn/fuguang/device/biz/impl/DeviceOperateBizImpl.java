package cn.fuguang.device.biz.impl;

import cn.fuguang.api.device.req.CreateOrderOpenGateReqDTO;
import cn.fuguang.device.biz.DeviceOperateBiz;
import cn.fuguang.device.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class DeviceOperateBizImpl implements DeviceOperateBiz {


    @Resource
    private DeviceService deviceService;

    @Override
    public void createOrderOpenGate(CreateOrderOpenGateReqDTO reqDTO) {

        //参数校验
        deviceService.checkDeviceStatus(reqDTO.getDeviceId(), reqDTO.getGateId());

        //开门前重量缓存
        deviceService.doCreateOpenGateOrderCache(reqDTO);

        //模拟调用通信服务开门
        log.info("开始模拟开仓门操作，设备ID：{}仓门ID：{}订单号：{}", reqDTO.getDeviceId(), reqDTO.getGateId(), reqDTO.getOrderNo());
        
        // 模拟设备响应延迟
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.error("模拟开仓门延迟异常", e);
        }
        
        // 记录开仓门日志，模拟设备响应
        log.info("模拟开仓门成功，设备ID：{}仓门ID：{}订单号：{}", reqDTO.getDeviceId(), reqDTO.getGateId(), reqDTO.getOrderNo());
        
        // 模拟更新设备状态为开门状态
        log.info("模拟更新设备状态为开门状态，设备ID：{}仓门ID：{}", reqDTO.getDeviceId(), reqDTO.getGateId());
    }





}
