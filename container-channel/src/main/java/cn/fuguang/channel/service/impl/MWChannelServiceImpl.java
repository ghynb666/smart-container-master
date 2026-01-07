package cn.fuguang.channel.service.impl;

import cn.fuguang.channel.pojo.dto.MwSendSmsResDTO;
import cn.fuguang.channel.service.MWChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MWChannelServiceImpl implements MWChannelService {

    @Override
    public MwSendSmsResDTO sendSingle() {
        MwSendSmsResDTO res = new MwSendSmsResDTO();
        res.setResult(0);
        res.setDesc("发送成功");
        res.setMsgId(System.currentTimeMillis());
        res.setCustId("demo_cust_id");
        log.info("短信发送成功");
        return res;
    }
}
