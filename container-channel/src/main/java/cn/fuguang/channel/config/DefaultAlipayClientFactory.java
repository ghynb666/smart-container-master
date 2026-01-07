package cn.fuguang.channel.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
@Slf4j
@Data
public class DefaultAlipayClientFactory {


    private AlipayClient alipayClient;

    @Resource
    private AlipayChannelConfig alipayChannelConfig;

    @PostConstruct
    public void postConstructor(){
        AlipayConfig config = new AlipayConfig();
        config.setServerUrl(alipayChannelConfig.getServiceUrl());
        config.setAppId(alipayChannelConfig.getAppId());
        config.setAlipayPublicKey(alipayChannelConfig.getAlipayPublicKey());
        config.setPrivateKey(alipayChannelConfig.getMerchantPrivateKey());
        config.setCharset(alipayChannelConfig.getCharset());
        config.setFormat(alipayChannelConfig.getFormat());
        config.setSignType(alipayChannelConfig.getSignType());
        try {
            alipayClient = new DefaultAlipayClient(config);
        } catch (AlipayApiException e) {
            log.error("创建阿里连接异常");
        }
    }

}
