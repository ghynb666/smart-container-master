package cn.fuguang.order.pojo.vo.res;

import lombok.Data;

import java.io.Serializable;

@Data
public class SeckillCouponRes implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 秒杀订单号
     */
    private String orderNo;

    /**
     * 秒杀状态 0:处理中 1:成功 2:失败
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String message;

}