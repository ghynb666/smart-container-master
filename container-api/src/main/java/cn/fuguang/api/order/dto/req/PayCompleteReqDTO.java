package cn.fuguang.api.order.dto.req;

import lombok.Builder;
import lombok.Data;

/**
 * 支付完成请求DTO
 */
@Data
@Builder
public class PayCompleteReqDTO {
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 实际支付金额（分）
     */
    private Long actualPayAmount;
    
    /**
     * 支付方式
     */
    private String payMethod;
    
    /**
     * 支付流水号
     */
    private String paySerialNo;
    
    /**
     * 购买的商品信息，JSON格式
     */
    private String purchasedGoodsJson;
}