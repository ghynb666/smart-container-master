package cn.fuguang.order.controller;

import cn.fuguang.order.biz.SmsBiz;
import cn.fuguang.order.pojo.vo.req.SendValidateSmsReq;
import cn.fuguang.order.pojo.vo.res.SendValidateSmsRes;
import cn.fuguang.web.BaseResult;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 验证码接口
 */
@RestController
@RequestMapping("customer")
@Slf4j
public class SmsController {

    @Resource
    private SmsBiz smsBiz;

    /**
     * 发送短信接口
     */
    @PostMapping("sendValidateSms")
    private BaseResult<SendValidateSmsRes> sendValidateSms(@RequestBody @Valid SendValidateSmsReq req){
        log.info("接收发送验证码请求 req:{}", JSONObject.toJSONString(req));
        SendValidateSmsRes res;
        try {
            // TODO: 2024/6/1 此处应该要对ip和手机号进行限流以后再做这一部分
            res = smsBiz.sendValidateSms(req);
        } catch (Exception e) {
            log.error("发送验证码 系统异常" + e);
            return BaseResult.fail("系统异常, 请稍后重试");
        }
        log.info("发送验证码结果 res:{}", JSONObject.toJSONString(res));
        return BaseResult.success(res);
    }




}
