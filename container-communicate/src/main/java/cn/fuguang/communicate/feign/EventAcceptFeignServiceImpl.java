package cn.fuguang.communicate.feign;

import cn.fuguang.api.channel.dto.res.AgreementSignResDTO;
import cn.fuguang.api.communicate.EventAcceptFeignService;
import cn.fuguang.api.communicate.dto.req.EventAcceptReqDTO;
import cn.fuguang.communicate.biz.EventAcceptBiz;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.feign.BaseResponse;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/communicate")
public class EventAcceptFeignServiceImpl implements EventAcceptFeignService {

    @Resource
    private EventAcceptBiz eventAcceptBiz;

    // TODO: 2024/8/31 日志全部要换成切面
    /**
     * 接收消息
     */
    @PostMapping("/eventAccept")
    public BaseResponse<Void> eventAccept(EventAcceptReqDTO reqDTO){

        log.info("[eventAccept] receive msg req:{}", JSONObject.toJSONString(reqDTO));
        try {
            eventAcceptBiz.eventAccept(reqDTO);
        } catch (ContainerException e) {
            log.error("[eventAccept] business error" + e);
            return BaseResponse.fail(e.getMessage());
        } catch (Exception e){
            log.error("[eventAccept] system error" + e);
            return BaseResponse.fail(e.getMessage());
        }
        log.info("[eventAccept] success");
        return BaseResponse.success();
    }

}
