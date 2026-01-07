package cn.fuguang.api.channel.dto.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class SendSmsReqDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 短信内容
     */
    private String content;
}
