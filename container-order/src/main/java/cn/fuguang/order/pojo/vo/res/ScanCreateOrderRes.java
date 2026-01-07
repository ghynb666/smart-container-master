package cn.fuguang.order.pojo.vo.res;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ScanCreateOrderRes implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 微信需确认订单小程序所需参数
     */
    Map<String, String> confirmExtraData;

    /**
     * 支付宝需确认订单小程序所需参数
     */
    private String signStr;

}
