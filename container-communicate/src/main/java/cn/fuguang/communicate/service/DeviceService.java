package cn.fuguang.communicate.service;

import cn.fuguang.api.device.DeviceInfoFeignService;
import cn.fuguang.api.device.DeviceOperateFeignService;
import cn.fuguang.api.device.req.CreateOrderOpenGateReqDTO;
import cn.fuguang.api.device.req.CheckDeviceStatusReqDTO;
import cn.fuguang.feign.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 设备服务集成类
 */
@Service
@Slf4j
public class DeviceService {
    
    @Autowired
    private DeviceOperateFeignService deviceOperateFeignService;
    
    @Autowired
    private DeviceInfoFeignService deviceInfoFeignService;
    
    /**
     * 调用设备服务进行开门操作
     * @param deviceSn 设备SN
     * @param orderId 订单ID
     * @param gateNumber 柜门编号
     */
    public void createOrderOpenGate(String deviceSn, String orderId, int gateNumber) {
        try {
            CreateOrderOpenGateReqDTO reqDTO = new CreateOrderOpenGateReqDTO();
            reqDTO.setDeviceId(deviceSn);
            reqDTO.setOrderNo(orderId);
            reqDTO.setGateId(String.valueOf(gateNumber));
            
            deviceOperateFeignService.createOrderOpenGate(reqDTO);
            log.info("调用设备服务开门成功：设备SN={}, 订单ID={}, 柜门编号={}", deviceSn, orderId, gateNumber);
        } catch (Exception e) {
            log.error("调用设备服务开门失败：设备SN={}, 订单ID={}, 柜门编号={}, 异常信息={}", 
                    deviceSn, orderId, gateNumber, e.getMessage(), e);
        }
    }
    
    /**
     * 调用设备服务检查设备状态
     * @param deviceSn 设备SN
     * @return 设备状态响应
     */
    public BaseResponse checkDeviceStatus(String deviceSn) {
        try {
            CheckDeviceStatusReqDTO reqDTO = CheckDeviceStatusReqDTO.builder()
                    .deviceId(deviceSn)
                    .build();
            
            BaseResponse response = deviceInfoFeignService.checkDeviceStatus(reqDTO);
            log.info("调用设备服务检查状态成功：设备SN={}, 响应={}", deviceSn, response);
            return response;
        } catch (Exception e) {
            log.error("调用设备服务检查状态失败：设备SN={}, 异常信息={}", deviceSn, e.getMessage(), e);
            return null;
        }
    }
}