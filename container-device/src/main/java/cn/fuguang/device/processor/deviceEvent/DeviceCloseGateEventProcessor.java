package cn.fuguang.device.processor.deviceEvent;

import cn.fuguang.enums.EventTypeEnum;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class DeviceCloseGateEventProcessor extends DeviceEventAbstractProcessor {
    @Override
    public String getProcessorType() {
        return EventTypeEnum.CLOSE_GATE.name();
    }

    @Override
    public void doDeviceEvent(JSONObject params) {

    }

}
