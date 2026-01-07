package cn.fuguang.api.channel;

import cn.fuguang.api.channel.dto.req.AgreementSignReqDTO;
import cn.fuguang.api.channel.dto.req.OauthTokenReqDTO;
import cn.fuguang.api.channel.dto.res.AgreementSignResDTO;
import cn.fuguang.api.channel.dto.res.OauthTokenResDTO;
import cn.fuguang.feign.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "container-channel", contextId = "ali-pay")
public interface AliPayFeignService {

    @ResponseBody
    @PostMapping("/channel/agreementSign")
    BaseResponse<AgreementSignResDTO> agreementSign(@RequestBody AgreementSignReqDTO reqDTO);

    @ResponseBody
    @PostMapping("/channel/oauthToken")
    BaseResponse<OauthTokenResDTO> oauthToken(@RequestBody OauthTokenReqDTO reqDTO);
}
