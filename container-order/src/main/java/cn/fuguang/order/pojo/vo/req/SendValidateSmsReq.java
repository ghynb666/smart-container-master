package cn.fuguang.order.pojo.vo.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class SendValidateSmsReq implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 手机号
     */
    @NotEmpty(message = "mobile不能为空")
    private String mobile;

    /**
     * 验证类型
     */
    @NotEmpty(message = "validateType不能为空")
    private String validateType;


}
