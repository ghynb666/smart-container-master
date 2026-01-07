package cn.fuguang.api.device;

import cn.fuguang.api.device.req.CreateOrderOpenGateReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "container-device",contextId = "device-operate")
public interface DeviceOperateFeignService {

    /**
     * 接收客户订单开门请求
     */
    @ResponseBody
    @PostMapping("/device/createOrderOpenGate")
    void createOrderOpenGate(@RequestBody CreateOrderOpenGateReqDTO reqDTO);
}
