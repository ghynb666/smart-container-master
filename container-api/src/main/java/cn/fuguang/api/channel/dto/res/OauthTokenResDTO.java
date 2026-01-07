package cn.fuguang.api.channel.dto.res;

import lombok.Data;

import java.io.Serializable;

@Data
public class OauthTokenResDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 支付宝用户唯一标识
     */
    private String openId;

    //有什么歌好听点
    /**
     * 交换令牌
     */
    private String accessToke;

    /**
     * 令牌有效期
     */
    private String expiresIn;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 刷新令牌有效期
     */
    private String reExpiresIn;
}
