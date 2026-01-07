package cn.fuguang.device.feign;

import cn.fuguang.api.device.DeviceOperateFeignService;
import cn.fuguang.api.device.req.CreateOrderOpenGateReqDTO;
import cn.fuguang.device.biz.DeviceOperateBiz;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Service
@Slf4j
public class DeviceOperateFeignServiceImpl implements DeviceOperateFeignService {

    @Resource
    private DeviceOperateBiz deviceOperateBiz;


    @Override
    public void createOrderOpenGate(CreateOrderOpenGateReqDTO reqDTO) {
        log.info("接收到container-order客户订单开门请求 req:{}", JSONObject.toJSONString(reqDTO));
        try {
            deviceOperateBiz.createOrderOpenGate(reqDTO);
        } catch (Exception e) {
            log.error("接收到container-order客户订单开门请求 系统异常" + e);
        }
        log.info("接收到container-order客户订单开门请求处理完成 req:{}", JSONObject.toJSONString(reqDTO));
    }
}
