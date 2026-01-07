package cn.fuguang.order.pojo.vo.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class AliAuthReq implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 用户对应用授权后得到，即下程序获取到的auth_code值
     */
    @NotEmpty(message = "authCode not empty")
    private String authCode;

}
