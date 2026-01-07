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

        //调用通信服务开门

        //组装请求参数  今天先到这


    }





}
