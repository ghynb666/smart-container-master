package cn.fuguang.order.biz.impl;

import cn.fuguang.entity.VerifyCodeEntity;
import cn.fuguang.enums.OrderTypeEnum;
import cn.fuguang.enums.ValidateSmsType;
import cn.fuguang.order.biz.SmsBiz;
import cn.fuguang.order.pojo.vo.req.SendValidateSmsReq;
import cn.fuguang.order.pojo.vo.res.SendValidateSmsRes;
import cn.fuguang.order.service.OrderService;
import cn.fuguang.order.service.VerifyCodeService;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SmsBizImpl implements SmsBiz {


    @Resource
    private VerifyCodeService verifyCodeService;

    @Resource
    private OrderService orderService;


    @Override
    public SendValidateSmsRes sendValidateSms(SendValidateSmsReq req) {

        SendValidateSmsRes sendValidateSmsRes = new SendValidateSmsRes();

        //生成验证码
        String verifyCode = verifyCodeService.createVerifyCode();

        //生成验证码订单号
        String orderNo = orderService.createOrderNo(OrderTypeEnum.VERITY_ORDER);

        //构建实体
        VerifyCodeEntity verifyCodeEntity = verifyCodeService.buildVerifyCodeEntity(req.getMobile(), verifyCode, orderNo);

        //验证码入库
        verifyCodeService.save(verifyCodeEntity);

        //获取短信模版
        String template = ValidateSmsType.REGISTER.getTemplate();

        //补充模版信息
        String msg = template.replace("${code}", verifyCode).
                replace("${expireTime}", DateUtil.format(verifyCodeEntity.getInvalidTime(), DatePattern.NORM_DATETIME_PATTERN));


        //保存请求发短信内容


        //调用通道服务发送短信


        sendValidateSmsRes.setOrderNo(orderNo);
        return sendValidateSmsRes;
    }
}
