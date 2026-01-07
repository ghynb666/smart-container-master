package cn.fuguang.order.feign;

import cn.fuguang.api.order.OrderFeignService;
import cn.fuguang.api.order.dto.req.AgreementSignCallBackReqDTO;
import cn.fuguang.order.biz.OrderBiz;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Service
@Slf4j
public class OrderFeignServiceImpl implements OrderFeignService {


    @Resource
    private OrderBiz orderBiz;

    @Override
    public void agreementSignCallBack(AgreementSignCallBackReqDTO reqDTO) {

        log.info("接收到channel预授权回调 req:{}", JSONObject.toJSONString(reqDTO));
        try {
            orderBiz.agreementSignCallBack(reqDTO);
        } catch (Exception e) {
            log.error("接收到channel预授权回调 系统异常" + e);
        }
        log.info("接收到channel预授权回调处理完成 req:{}", JSONObject.toJSONString(reqDTO));

    }
}
