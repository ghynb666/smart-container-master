package cn.fuguang.channel.fegin;

import cn.fuguang.api.channel.MWFeignService;
import cn.fuguang.api.channel.dto.req.SendSmsReqDTO;
import cn.fuguang.channel.pojo.dto.MwSendSmsResDTO;
import cn.fuguang.channel.service.MWChannelService;
import cn.fuguang.feign.BaseResponse;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class MWFeignServiceImpl implements MWFeignService {

    @Resource
    private MWChannelService mwChannelService;

    @Override
    public BaseResponse sendSms(SendSmsReqDTO reqDTO) {
        log.info("通道接收发送短信参数:{}", JSONObject.toJSONString(reqDTO));
        try {
            MwSendSmsResDTO sendRes = mwChannelService.sendSingle();
            if (sendRes == null || sendRes.getResult() == null || sendRes.getResult() != 0) {
                String code = sendRes != null ? String.valueOf(sendRes.getResult()) : "-1";
                String msg = sendRes != null ? sendRes.getDesc() : "发送失败";
                return BaseResponse.fail(code, msg);
            }
        } catch (Exception e) {
            log.error("通道接收发送短信 系统异常" + e);
            return BaseResponse.fail(e.getMessage());
        }
        log.info("通道发送短信成功");
        return BaseResponse.success();
    }
}
