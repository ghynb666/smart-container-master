package cn.fuguang.order.pojo.vo.res;

import lombok.Data;

import java.io.Serializable;

@Data
public class SendValidateSmsRes implements Serializable {

    private static final long serialVersionUID = -1L;


    /**
     * 订单号
     */
    private String orderNo;


}
