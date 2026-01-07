package cn.fuguang.api.channel;

import cn.fuguang.api.channel.dto.req.SendSmsReqDTO;
import cn.fuguang.feign.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "container-channel", contextId = "mw")
public interface MWFeignService {

    @ResponseBody
    @PostMapping("/channel/sendSms")
    BaseResponse sendSms(SendSmsReqDTO reqDTO);
}
