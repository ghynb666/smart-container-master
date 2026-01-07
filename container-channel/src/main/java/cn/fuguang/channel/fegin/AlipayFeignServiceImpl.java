package cn.fuguang.channel.fegin;

import cn.fuguang.api.channel.AliPayFeignService;
import cn.fuguang.api.channel.dto.req.AgreementSignReqDTO;
import cn.fuguang.api.channel.dto.req.OauthTokenReqDTO;
import cn.fuguang.api.channel.dto.res.AgreementSignResDTO;
import cn.fuguang.api.channel.dto.res.OauthTokenResDTO;
import cn.fuguang.channel.biz.AlipayBiz;
import cn.fuguang.feign.BaseResponse;
import cn.fuguang.web.BaseResult;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Service
@Slf4j
public class AlipayFeignServiceImpl implements AliPayFeignService {

    @Resource
    private AlipayBiz alipayBiz;

    @Override
    public BaseResponse<AgreementSignResDTO> agreementSign(AgreementSignReqDTO reqDTO) {
        log.info("通道预授权下单 req:{}", JSONObject.toJSONString(reqDTO));
        AgreementSignResDTO res;
        try {
            res = alipayBiz.agreementSign(reqDTO);
        } catch (Exception e) {
            log.error("通道预授权下单 系统异常" + e);
            return BaseResponse.fail("系统异常, 请稍后重试");
        }
        log.info("通道预授权下单 res:{}", JSONObject.toJSONString(res));
        return BaseResponse.success(res);
    }

    @Override
    public BaseResponse<OauthTokenResDTO> oauthToken(OauthTokenReqDTO reqDTO) {
        log.info("通道用户支付宝认证 req:{}", JSONObject.toJSONString(reqDTO));
        OauthTokenResDTO res;
        try {
            res = alipayBiz.oauthToken(reqDTO);
        } catch (Exception e) {
            log.error("通道用户支付宝认证 系统异常" + e);
            return BaseResponse.fail(e.getMessage());
        }
        log.info("通道用户支付宝认证 res:{}", JSONObject.toJSONString(res));
        return BaseResponse.success(res);
    }
}
