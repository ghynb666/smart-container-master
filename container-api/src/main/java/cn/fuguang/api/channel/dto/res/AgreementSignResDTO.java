package cn.fuguang.api.channel.dto.res;

import lombok.Data;

import java.io.Serializable;

@Data
public class AgreementSignResDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 前段唤起阿里签约页signStr
     */
    private String signStr;
}
