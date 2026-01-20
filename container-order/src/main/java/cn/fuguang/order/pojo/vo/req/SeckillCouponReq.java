package cn.fuguang.order.pojo.vo.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class SeckillCouponReq implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 客户id
     */
    @NotEmpty(message = "customerId not empty")
    private String customerId;

    /**
     * 优惠券配置id
     */
    @NotEmpty(message = "couponConfigId not empty")
    private String couponConfigId;

    /**
     * 滑块验证令牌
     */
    @NotEmpty(message = "verifyToken not empty")
    private String verifyToken;

}