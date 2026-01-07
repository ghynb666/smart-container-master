package cn.fuguang.order.biz;


import cn.fuguang.order.pojo.vo.req.SendValidateSmsReq;
import cn.fuguang.order.pojo.vo.res.SendValidateSmsRes;

public interface SmsBiz {

    /**
     * 发送验证码
     */
    SendValidateSmsRes sendValidateSms(SendValidateSmsReq req);
}
