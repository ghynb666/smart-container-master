package cn.fuguang.order.service;

import java.util.Map;

/**
 * 支付金额计算服务
 */
public interface PayAmountCalculateService {

    /**
     * 根据购买的商品计算实际支付金额
     * @param purchasedGoods 购买的商品信息，key为商品ID，value为购买数量
     * @return 实际支付金额（分）
     */
    long calculateActualPayAmount(Map<String, Integer> purchasedGoods);
}