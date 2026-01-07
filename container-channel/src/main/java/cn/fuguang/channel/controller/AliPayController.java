package cn.fuguang.channel.controller;

import cn.fuguang.channel.biz.AlipayBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/alipay")
public class AliPayController {

    @Resource
    private AlipayBiz alipayBiz;

    /**
     * 接收支付宝签约成功回调
     */
    @PostMapping("/agreementSignCallBack")
    public void agreementSignCallBack(HttpServletRequest request){
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            log.info("支付宝预授权签约回调参数:{}", parameterMap);

            alipayBiz.doAgreementSignCallBack(parameterMap);
        } catch (Exception e) {
            log.error("支付宝预授权签约回调处理异常 e" + e);
        }
    }
}
