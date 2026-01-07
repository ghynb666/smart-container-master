package cn.fuguang.api.device;

import cn.fuguang.api.device.req.CheckDeviceStatusReqDTO;
import cn.fuguang.feign.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "container-device",contextId = "device-info")
public interface DeviceInfoFeignService {

    @ResponseBody
    @PostMapping("/device/checkDeviceStatus")
    BaseResponse checkDeviceStatus(@RequestBody CheckDeviceStatusReqDTO reqDTO);
}
