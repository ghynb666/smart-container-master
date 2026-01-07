package cn.fuguang.order.biz.impl;

import cn.fuguang.api.channel.AliPayFeignService;
import cn.fuguang.api.channel.dto.req.OauthTokenReqDTO;
import cn.fuguang.api.channel.dto.res.OauthTokenResDTO;
import cn.fuguang.entity.CustomerEntity;
import cn.fuguang.enums.OrderStatusEnum;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.feign.BaseResponse;
import cn.fuguang.order.biz.CustomerBiz;
import cn.fuguang.order.pojo.vo.req.AliAuthReq;
import cn.fuguang.order.pojo.vo.res.AliAuthRes;
import cn.fuguang.order.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class CustomerBizImpl implements CustomerBiz {


    @Resource
    private AliPayFeignService aliPayFeignService;

    @Resource
    private CustomerService customerService;


    @Override
    public AliAuthRes aliAuth(AliAuthReq req) {
        AliAuthRes aliAuthRes = new AliAuthRes();

        BaseResponse<OauthTokenResDTO> baseResponse = aliPayFeignService.oauthToken(OauthTokenReqDTO.builder().authCode(req.getAuthCode()).build());

        if (!baseResponse.isSuccess()){
            log.error("调用channel服务阿里用户认证失败");
            throw ContainerException.CUSTOMER_AUTH_ERROR.newInstance("用户认证失败");
        }
        OauthTokenResDTO oauthTokenResDTO = baseResponse.getData();

        CustomerEntity customerEntity;
        try {
            customerEntity = customerService.queryCustomerByAliOpenId(oauthTokenResDTO.getOpenId());
        } catch (ContainerException e) {
            //查询数据库是否有此用户 判断是否第一次登陆小程序
            if (e.getDefineCode().equals(ContainerException.DATE_NOT_EXIST_ERROR.getDefineCode())){
                log.info("用户首次进行支付宝登陆认证 openId:{}", oauthTokenResDTO.getOpenId());
                aliAuthRes.setBindStatus(false);
                aliAuthRes.setOpenId(oauthTokenResDTO.getOpenId());
                aliAuthRes.setAccessToken(oauthTokenResDTO.getAccessToke());
                return aliAuthRes;
            } else {
                throw ContainerException.DATABASE_QUERY_ERROR;
            }
        }
        aliAuthRes.setBindStatus(true);
        aliAuthRes.setOpenId(oauthTokenResDTO.getOpenId());
        aliAuthRes.setMobile(customerEntity.getMobile());
        aliAuthRes.setCustomerId(customerEntity.getCustomerId());
        aliAuthRes.setAccessToken(oauthTokenResDTO.getAccessToke());
        return aliAuthRes;
    }
}
