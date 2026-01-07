package cn.fuguang.channel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alipay")
@Data
public class AlipayChannelConfig {

    /**
     * 阿里服务地址
     */
    private String serviceUrl;

    /**
     * appid
     */
    private String appId;

    /**
     * 商户私钥
     */
    private String merchantPrivateKey;

    /**
     * ali公钥
     */
    private String alipayPublicKey;

    /**
     * 加密类型
     */
    private String signType;

    /**
     * 字符集
     */
    private String charset;

    /**
     * 传输数据类型
     */
    private String format;

    /**
     * 预授权回调地址
     */
    private String agreeSignNotifyUrl;
}
