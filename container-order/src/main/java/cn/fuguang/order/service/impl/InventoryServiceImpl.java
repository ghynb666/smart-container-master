package cn.fuguang.order.service.impl;

import cn.fuguang.order.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 库存管理服务实现类
 */
@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    // 模拟设备库存数据，key为设备ID，value为该设备的商品库存映射（key为商品ID，value为库存数量）
    private static final Map<String, Map<String, Integer>> DEVICE_INVENTORY_MAP = new HashMap<>();
    
    static {
        // 初始化模拟库存数据
        Map<String, Integer> inventoryMap = new HashMap<>();
        inventoryMap.put("PROD001", 10); // 可乐 10瓶
        inventoryMap.put("PROD002", 20); // 矿泉水 20瓶
        inventoryMap.put("PROD003", 15); // 饼干 15包
        inventoryMap.put("PROD004", 8);  // 薯片 8包
        inventoryMap.put("PROD005", 12); // 方便面 12桶
        
        // 为设备DEV001设置库存
        DEVICE_INVENTORY_MAP.put("DEV001", inventoryMap);
        
        // 为其他设备设置相同的初始库存
        DEVICE_INVENTORY_MAP.put("DEV002", new HashMap<>(inventoryMap));
        DEVICE_INVENTORY_MAP.put("DEV003", new HashMap<>(inventoryMap));
    }

    @Override
    public boolean deductInventory(String deviceId, Map<String, Integer> purchasedGoods) {
        log.info("开始扣减库存，设备ID：{}商品信息：{}", deviceId, purchasedGoods);
        
        // 获取设备库存
        Map<String, Integer> inventoryMap = DEVICE_INVENTORY_MAP.computeIfAbsent(deviceId, k -> new HashMap<>());
        
        // 检查库存是否充足
        for (Map.Entry<String, Integer> entry : purchasedGoods.entrySet()) {
            String productId = entry.getKey();
            int purchaseQuantity = entry.getValue();
            
            int currentInventory = inventoryMap.getOrDefault(productId, 0);
            if (currentInventory < purchaseQuantity) {
                log.error("库存不足，设备ID：{}商品ID：{}当前库存：{}购买数量：{}", 
                        deviceId, productId, currentInventory, purchaseQuantity);
                return false;
            }
        }
        
        // 扣减库存
        for (Map.Entry<String, Integer> entry : purchasedGoods.entrySet()) {
            String productId = entry.getKey();
            int purchaseQuantity = entry.getValue();
            
            int currentInventory = inventoryMap.getOrDefault(productId, 0);
            int newInventory = currentInventory - purchaseQuantity;
            inventoryMap.put(productId, newInventory);
            
            log.info("库存扣减成功，设备ID：{}商品ID：{}扣减前：{}扣减数量：{}扣减后：{}", 
                    deviceId, productId, currentInventory, purchaseQuantity, newInventory);
        }
        
        log.info("库存扣减完成，设备ID：{}更新后的库存：{}", deviceId, inventoryMap);
        return true;
    }

    @Override
    public int getInventory(String deviceId, String productId) {
        Map<String, Integer> inventoryMap = DEVICE_INVENTORY_MAP.getOrDefault(deviceId, new HashMap<>());
        return inventoryMap.getOrDefault(productId, 0);
    }
}