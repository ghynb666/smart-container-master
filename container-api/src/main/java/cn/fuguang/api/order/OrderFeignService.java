package cn.fuguang.api.order;

import cn.fuguang.api.order.dto.req.AgreementSignCallBackReqDTO;
import cn.fuguang.api.order.dto.req.PayCompleteReqDTO;
import cn.fuguang.feign.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "container-order")
public interface OrderFeignService {

    /**
     * 接收channel服务预授权回调
     */
    @PostMapping("/agreementSignCallBack")
    @ResponseBody
    void agreementSignCallBack(@RequestBody AgreementSignCallBackReqDTO reqDTO);
    
    /**
     * 处理支付完成
     */
    @PostMapping("/payComplete")
    @ResponseBody
    void payComplete(@RequestBody PayCompleteReqDTO reqDTO);
}
