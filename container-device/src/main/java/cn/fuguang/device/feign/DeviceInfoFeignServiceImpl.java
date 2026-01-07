package cn.fuguang.device.feign;

import cn.fuguang.api.channel.dto.res.AgreementSignResDTO;
import cn.fuguang.api.device.DeviceInfoFeignService;
import cn.fuguang.api.device.req.CheckDeviceStatusReqDTO;
import cn.fuguang.device.biz.DeviceInfoBiz;
import cn.fuguang.feign.BaseResponse;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Service
@Slf4j
public class DeviceInfoFeignServiceImpl implements DeviceInfoFeignService {

    @Resource
    private DeviceInfoBiz deviceInfoBiz;

    @Override
    public BaseResponse checkDeviceStatus(CheckDeviceStatusReqDTO reqDTO) {
        log.info("校验设备状态 req:{}", JSONObject.toJSONString(reqDTO));

        try {
            deviceInfoBiz.checkDeviceStatus(reqDTO);
        } catch (Exception e) {
            log.error("校验设备状态 系统异常" + e);
            return BaseResponse.fail(e.getMessage());
        }
        log.info("校验设备状态成功");
        return BaseResponse.success();
    }
}
