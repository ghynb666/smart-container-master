package cn.fuguang.channel.service.impl;

import cn.fuguang.channel.config.AlipayChannelConfig;
import cn.fuguang.channel.config.DefaultAlipayClientFactory;
import cn.fuguang.channel.service.AlipayChannelService;
import cn.fuguang.exception.ContainerException;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AccessParams;
import com.alipay.api.domain.AlipayUserAgreementPageSignModel;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserAgreementPageSignRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserAgreementPageSignResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
@Slf4j
public class AlipayChannelServiceImpl implements AlipayChannelService {

    @Resource
    private AlipayChannelConfig alipayChannelConfig;

    @Resource
    private DefaultAlipayClientFactory defaultAlipayClientFactory;


    @Override
    public String agreementSign(String orderNo, String mobile) {
        AlipayUserAgreementPageSignModel model = buildAgreementSignModel(orderNo, mobile);

        AlipayUserAgreementPageSignRequest request = new AlipayUserAgreementPageSignRequest();
        request.setBizModel(model);
        request.setNotifyUrl(alipayChannelConfig.getAgreeSignNotifyUrl());
        AlipayUserAgreementPageSignResponse response;
        try {
            log.info("调用阿里预授权下单接口 req:{}", JSONObject.toJSONString(request));
            response = defaultAlipayClientFactory.getAlipayClient().sdkExecute(request);
            log.info("调用阿里预授权下单接口 res:{}", JSONObject.toJSONString(response));
        } catch (AlipayApiException e) {
            log.error("调用阿里预授权下单接口异常 e:" + e);
            throw ContainerException.REMOTE_CALL_ERROR.newInstance("调用阿里预授权下单接口异常");
        }

        if (response.isSuccess()) {
            try {
                return URLEncoder.encode(response.getBody(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("调用阿里预授权下单接口URLEncoder失败");
                throw ContainerException.REMOTE_CALL_ERROR.newInstance("调用阿里预授权下单接口URLEncoder失败");
            }
        } else {
            log.error("调用阿里预授权下单接口失败");
            throw ContainerException.REMOTE_CALL_ERROR.newInstance(response.getMsg());
        }

    }

    @Override
    public AlipaySystemOauthTokenResponse oauthToken(String authCode) {

        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        AlipaySystemOauthTokenResponse response;

        // 设置授权方式
        request.setGrantType("authorization_code");
        request.setCode(authCode);
        try {
            log.info("调用阿里用户认证接口 req:{}", JSONObject.toJSONString(request));
            response = defaultAlipayClientFactory.getAlipayClient().execute(request);
            log.info("调用阿里用户认证接口 res:{}", JSONObject.toJSONString(response));
        } catch (AlipayApiException e) {
            log.error("调用阿里用户认证接口异常 e:" + e);
            throw ContainerException.REMOTE_CALL_ERROR.newInstance("调用阿里用户认证接口异常");
        }

        if (response.isSuccess()) {
            return response;
        } else {
            log.error("调用阿里用户认证接口失败");
            throw ContainerException.REMOTE_CALL_ERROR.newInstance(response.getMsg());
        }
    }


    /**
     * 构建阿里预授权订单请求参数
     */
    private AlipayUserAgreementPageSignModel buildAgreementSignModel(String orderNo, String mobile) {//暂停
        AlipayUserAgreementPageSignModel model = new AlipayUserAgreementPageSignModel();

        //设置个人签约产品码
        model.setPersonalProductCode("ONE_TIME_AUTH_P");
        model.setProductCode("ONE_TIME_AUTH");

        // 设置签约协议场景
        model.setSignScene("INDUSTRY|VENDING_MACHINE");

        // 设置请按当前接入的方式进行填充
        AccessParams accessParams = new AccessParams();
        accessParams.setChannel("ALIPAYAPP");
        model.setAccessParams(accessParams);

        // 设置商户签约号
        model.setExternalAgreementNo(orderNo);

        //签约有效时间限制，单位是秒，有效范围是0-86400，商户传入此字段会用商户传入的值否则使用支付宝侧默认值，在有效时间外进行签约，会进行安全拦截
        model.setEffectTime(120L);

        //用户在商户网站的登录账号，用于在签约页面展示 传入电话号码
        model.setExternalLogonId(mobile);

        return model;
    }
}
