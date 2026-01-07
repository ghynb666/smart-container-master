package cn.fuguang.device.biz.impl;

import cn.fuguang.device.biz.CommunicateEventBiz;
import cn.fuguang.device.processor.deviceEvent.DeviceEventProcessor;
import cn.fuguang.device.processor.deviceEvent.DeviceEventProcessorFactory;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class CommunicateEventBizImpl implements CommunicateEventBiz {


    @Resource
    private DeviceEventProcessorFactory deviceEventProcessorFactory;

    @Override
    public void doDeviceEvent(JSONObject params) {

        //获取当前消息事件
        String event = (String) params.get("event");

        //获取事件处理器
        DeviceEventProcessor deviceEventProcessor = deviceEventProcessorFactory.search(event);

        //处理事件
        deviceEventProcessor.doDeviceEvent(params);
    }
}
