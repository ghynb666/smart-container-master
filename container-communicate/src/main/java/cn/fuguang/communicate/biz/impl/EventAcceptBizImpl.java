package cn.fuguang.communicate.biz.impl;

import cn.fuguang.api.communicate.dto.req.EventAcceptReqDTO;
import cn.fuguang.communicate.biz.EventAcceptBiz;
import cn.fuguang.utils.ValidationUtils;
import org.springframework.stereotype.Service;

@Service
public class EventAcceptBizImpl implements EventAcceptBiz {

    @Override
    public void eventAccept(EventAcceptReqDTO reqDTO) {
        ValidationUtils.validate(reqDTO);


    }


}
