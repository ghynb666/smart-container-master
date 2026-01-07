package cn.fuguang.device.processor.deviceEvent;

import com.alibaba.fastjson2.JSONObject;

public interface DeviceEventProcessor {

    /**
     * 获取事件类型
     */
    String getProcessorType();

    /**
     * 处理事件
     */
    void doDeviceEvent(JSONObject params);

}
