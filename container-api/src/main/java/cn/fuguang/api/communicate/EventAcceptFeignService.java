package cn.fuguang.api.communicate;

import cn.fuguang.api.communicate.dto.req.EventAcceptReqDTO;
import cn.fuguang.feign.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "container-communicate")
public interface EventAcceptFeignService {

    @ResponseBody
    @PostMapping("/communicate/eventAccept")
    BaseResponse<Void> eventAccept(@RequestBody EventAcceptReqDTO reqDTO);
}
