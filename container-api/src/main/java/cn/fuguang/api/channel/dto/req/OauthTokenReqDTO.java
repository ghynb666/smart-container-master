package cn.fuguang.api.channel.dto.req;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class OauthTokenReqDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 授权码
     */
    private String authCode;
}
