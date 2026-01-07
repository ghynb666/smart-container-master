package cn.fuguang.device.processor.deviceEvent;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


@Component
public class DeviceEventProcessorFactory  {
    @Resource
    private List<DeviceEventProcessor> deviceEventProcessorList;


    public DeviceEventProcessor search(String event) {

        for (DeviceEventProcessor deviceEventProcessor : deviceEventProcessorList) {
            String processorType = deviceEventProcessor.getProcessorType();
            if (event.equals(processorType)){
                return deviceEventProcessor;
            }
        }
        return null;

    }

}
