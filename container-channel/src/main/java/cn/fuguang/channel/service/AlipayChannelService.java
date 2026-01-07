package cn.fuguang.channel.service;

import com.alipay.api.response.AlipaySystemOauthTokenResponse;

import java.io.UnsupportedEncodingException;

public interface AlipayChannelService {

    /**
     * 预授权创建订单
     */
    String agreementSign(String orderNo, String mobile);

    /**
     * 调用支付宝用户认证
     */
    AlipaySystemOauthTokenResponse oauthToken(String authCode);
}
