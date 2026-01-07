package cn.fuguang.api.channel;

import cn.fuguang.feign.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "container-channel", contextId = "wechat-pay")
public interface WechatPayFeignService {

    //BaseResponse

}
