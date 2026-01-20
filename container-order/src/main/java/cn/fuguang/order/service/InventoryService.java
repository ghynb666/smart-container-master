package cn.fuguang.order.service;

import java.util.Map;

/**
 * 库存管理服务
 */
public interface InventoryService {

    /**
     * 根据购买的商品扣减库存
     * @param deviceId 设备ID
     * @param purchasedGoods 购买的商品信息，key为商品ID，value为购买数量
     * @return 是否扣减成功
     */
    boolean deductInventory(String deviceId, Map<String, Integer> purchasedGoods);
    
    /**
     * 查询商品库存
     * @param deviceId 设备ID
     * @param productId 商品ID
     * @return 库存数量
     */
    int getInventory(String deviceId, String productId);
}