package cn.fuguang.channel.biz;

import cn.fuguang.api.channel.dto.req.AgreementSignReqDTO;
import cn.fuguang.api.channel.dto.req.OauthTokenReqDTO;
import cn.fuguang.api.channel.dto.res.AgreementSignResDTO;
import cn.fuguang.api.channel.dto.res.OauthTokenResDTO;

import java.util.Map;

public interface AlipayBiz {
    AgreementSignResDTO agreementSign(AgreementSignReqDTO reqDTO);

    void doAgreementSignCallBack(Map<String, String[]> parameterMap);

    OauthTokenResDTO oauthToken(OauthTokenReqDTO reqDTO);
}
