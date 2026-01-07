package cn.fuguang.channel.biz.impl;

import cn.fuguang.api.channel.dto.req.AgreementSignReqDTO;
import cn.fuguang.api.channel.dto.req.OauthTokenReqDTO;
import cn.fuguang.api.channel.dto.res.AgreementSignResDTO;
import cn.fuguang.api.channel.dto.res.OauthTokenResDTO;
import cn.fuguang.api.order.OrderFeignService;
import cn.fuguang.api.order.dto.req.AgreementSignCallBackReqDTO;
import cn.fuguang.channel.biz.AlipayBiz;
import cn.fuguang.channel.config.AlipayChannelConfig;
import cn.fuguang.channel.service.AlipayChannelService;
import cn.fuguang.channel.service.CustomerAgreementSignService;
import cn.fuguang.channel.service.CustomerService;
import cn.fuguang.channel.service.OrderService;
import cn.fuguang.constants.MonitorConstants;
import cn.fuguang.entity.CustomerAgreementSignEntity;
import cn.fuguang.entity.CustomerEntity;
import cn.fuguang.exception.ContainerException;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AlipayBizImpl implements AlipayBiz {

    @Resource
    private CustomerAgreementSignService customerAgreementSignService;

    @Resource
    private CustomerService customerService;

    @Resource
    private AlipayChannelService alipayChannelService;

    @Resource
    private AlipayChannelConfig alipayChannelConfig;

    @Resource
    private OrderFeignService orderFeignService;

    @Override
    public AgreementSignResDTO agreementSign(AgreementSignReqDTO reqDTO) {

        AgreementSignResDTO agreementSignResDTO = new AgreementSignResDTO();

        //调用阿里预授权创建订单接口,获取前段唤起阿里签约页signStr
        String signStr = alipayChannelService.agreementSign(reqDTO.getOrderNo(), reqDTO.getMobile());
        agreementSignResDTO.setSignStr(signStr);
        return agreementSignResDTO;
    }

    @Override
    public void doAgreementSignCallBack(Map<String, String[]> parameterMap) {
        //验签
        Map<String, String> params = verifySign(parameterMap);

        //获取用户信息
        CustomerEntity customerEntity = customerService.queryCustomerByAliOpenId(params.get("alipay_user_id"));

        //构建用户签约信息
        CustomerAgreementSignEntity customerAgreementSignEntity = buildCustomerAgreementSignInfo(params, customerEntity);

        //保存用户签约信息
        customerAgreementSignService.saveEntity(customerAgreementSignEntity);

        //回调order服务签约状态
        orderFeignService.agreementSignCallBack(AgreementSignCallBackReqDTO.builder().orderNo(customerAgreementSignEntity.getOrderNo())
                .agreementStatus(customerAgreementSignEntity.getStatus()).build());


    }

    @Override
    public OauthTokenResDTO oauthToken(OauthTokenReqDTO reqDTO) {


        AlipaySystemOauthTokenResponse response = alipayChannelService.oauthToken(reqDTO.getAuthCode());

        OauthTokenResDTO oauthTokenResDTO = new OauthTokenResDTO();
        BeanUtils.copyProperties(response, oauthTokenResDTO);

        return oauthTokenResDTO;
    }

    private CustomerAgreementSignEntity buildCustomerAgreementSignInfo(Map<String, String> params, CustomerEntity customerEntity) {
        CustomerAgreementSignEntity customerAgreementSignEntity = new CustomerAgreementSignEntity();
        customerAgreementSignEntity.setCustomerId(customerEntity.getCustomerId());
        customerAgreementSignEntity.setStatus(params.get("status"));
        customerAgreementSignEntity.setOrderNo(params.get("external_agreement_no"));
        customerAgreementSignEntity.setAgreementNo(params.get("agreement_no"));
        customerAgreementSignEntity.setSignTime(DateUtil.parse(params.get("sign_time"), DatePattern.NORM_DATETIME_PATTERN));
        customerAgreementSignEntity.setAliOpenId(params.get("sign_time"));
        return customerAgreementSignEntity;
    }

    private Map<String, String> verifySign(Map<String, String[]> parameterMap) {
        Map<String, String> params = new HashMap<>(8);
        for (Map.Entry<String, String[]> stringEntry : parameterMap.entrySet()) {
            String[] values = stringEntry.getValue();
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(stringEntry.getKey(), valueStr);
        }


        log.info("支付宝回调参数解析:{}", params);
        boolean signVerified;
        try {
            signVerified = AlipaySignature.verifyV1(params, alipayChannelConfig.getAlipayPublicKey(), alipayChannelConfig.getCharset(), alipayChannelConfig.getSignType());
        } catch (AlipayApiException e) {
            throw ContainerException.VERIFY_ERROR.newInstance("支付宝回调验签异常");
        }

        if (!signVerified){
            log.error(MonitorConstants.SIGN_VERIFY_ERROR + "支付宝回调验签失败:{}", parameterMap);
            throw ContainerException.VERIFY_ERROR.newInstance("支付宝回调验签失败");
        }

        log.info("支付宝回调验签成功:{}", params);
        return params;
    }


}
