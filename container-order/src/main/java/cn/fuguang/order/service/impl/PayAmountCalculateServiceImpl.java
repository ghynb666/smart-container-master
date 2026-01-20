package cn.fuguang.order.service.impl;

import cn.fuguang.order.service.PayAmountCalculateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付金额计算服务实现类
 */
@Service
@Slf4j
public class PayAmountCalculateServiceImpl implements PayAmountCalculateService {

    // 模拟商品价格映射，key为商品ID，value为商品价格（分）
    private static final Map<String, Long> PRODUCT_PRICE_MAP = new HashMap<>();
    
    static {
        // 初始化模拟商品数据
        PRODUCT_PRICE_MAP.put("PROD001", 350L); // 可乐 3.5元
        PRODUCT_PRICE_MAP.put("PROD002", 200L); // 矿泉水 2元
        PRODUCT_PRICE_MAP.put("PROD003", 500L); // 饼干 5元
        PRODUCT_PRICE_MAP.put("PROD004", 600L); // 薯片 6元
        PRODUCT_PRICE_MAP.put("PROD005", 450L); // 方便面 4.5元
    }

    @Override
    public long calculateActualPayAmount(Map<String, Integer> purchasedGoods) {
        log.info("开始计算实际支付金额，商品信息：{}", purchasedGoods);
        
        // 计算总金额
        long totalAmount = 0;
        for (Map.Entry<String, Integer> entry : purchasedGoods.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            Long price = PRODUCT_PRICE_MAP.getOrDefault(productId, 0L);
            long productTotal = price * quantity;
            totalAmount += productTotal;
            
            log.info("商品ID：{}数量：{}单价：{}分，小计：{}分", productId, quantity, price, productTotal);
        }
        
        log.info("实际支付金额计算完成，总金额：{}分", totalAmount);
        return totalAmount;
    }
}