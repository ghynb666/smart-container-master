package cn.fuguang.order.pojo.vo.res;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class AliAuthRes implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 用户绑定状态
     */
    private Boolean bindStatus;

    /**
     * 用户openId
     */
    private String openId;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 用户编号
     */
    private String customerId;

    /**
     * 令牌
     */
    private String accessToken;
}
