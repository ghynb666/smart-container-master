package cn.fuguang.order.service.impl;

import cn.fuguang.order.service.ConsumptionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消费记录服务实现类
 */
@Service
@Slf4j
public class ConsumptionRecordServiceImpl implements ConsumptionRecordService {

    // 模拟消费记录数据，key为记录ID，value为消费记录详情
    private static final Map<String, Map<String, Object>> CONSUMPTION_RECORDS = new ConcurrentHashMap<>();
    
    // 模拟商品名称映射，key为商品ID，value为商品名称
    private static final Map<String, String> PRODUCT_NAME_MAP = new HashMap<>();
    
    static {
        // 初始化商品名称映射
        PRODUCT_NAME_MAP.put("PROD001", "可乐");
        PRODUCT_NAME_MAP.put("PROD002", "矿泉水");
        PRODUCT_NAME_MAP.put("PROD003", "饼干");
        PRODUCT_NAME_MAP.put("PROD004", "薯片");
        PRODUCT_NAME_MAP.put("PROD005", "方便面");
    }

    @Override
    public boolean generateConsumptionRecord(String orderNo, String customerId, String deviceId, long payAmount, Map<String, Integer> purchasedGoods) {
        log.info("开始生成消费记录，订单号：{}客户ID：{}设备ID：{}支付金额：{}分，商品信息：{}", 
                orderNo, customerId, deviceId, payAmount, purchasedGoods);
        
        // 构建消费记录
        Map<String, Object> record = new HashMap<>();
        String recordId = "REC" + System.currentTimeMillis();
        
        record.put("recordId", recordId);
        record.put("orderNo", orderNo);
        record.put("customerId", customerId);
        record.put("deviceId", deviceId);
        record.put("payAmount", payAmount);
        record.put("purchasedGoods", purchasedGoods);
        record.put("consumptionTime", System.currentTimeMillis());
        record.put("status", "SUCCESS");
        
        // 计算商品总价（用于记录）
        long totalPrice = calculateTotalPrice(purchasedGoods);
        record.put("totalPrice", totalPrice);
        
        // 保存消费记录
        CONSUMPTION_RECORDS.put(recordId, record);
        
        log.info("消费记录生成成功，记录ID：{}订单号：{}", recordId, orderNo);
        
        return true;
    }

    @Override
    public List<Map<String, Object>> queryConsumptionRecords(String customerId, int pageNum, int pageSize) {
        log.info("查询客户消费记录，客户ID：{}页码：{}每页数量：{}", customerId, pageNum, pageSize);
        
        // 筛选客户的消费记录
        List<Map<String, Object>> customerRecords = new ArrayList<>();
        for (Map<String, Object> record : CONSUMPTION_RECORDS.values()) {
            if (customerId.equals(record.get("customerId"))) {
                customerRecords.add(record);
            }
        }
        
        // 模拟分页处理
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, customerRecords.size());
        
        // 返回分页结果
        if (startIndex >= customerRecords.size()) {
            return new ArrayList<>();
        }
        
        List<Map<String, Object>> result = customerRecords.subList(startIndex, endIndex);
        log.info("查询客户消费记录成功，客户ID：{}查询到{}条记录，返回{}条记录", 
                customerId, customerRecords.size(), result.size());
        
        return result;
    }
    
    /**
     * 计算商品总价
     */
    private long calculateTotalPrice(Map<String, Integer> purchasedGoods) {
        // 模拟商品价格映射，key为商品ID，value为商品价格（分）
        Map<String, Long> productPriceMap = new HashMap<>();
        productPriceMap.put("PROD001", 350L); // 可乐 3.5元
        productPriceMap.put("PROD002", 200L); // 矿泉水 2元
        productPriceMap.put("PROD003", 500L); // 饼干 5元
        productPriceMap.put("PROD004", 600L); // 薯片 6元
        productPriceMap.put("PROD005", 450L); // 方便面 4.5元
        
        long totalPrice = 0;
        for (Map.Entry<String, Integer> entry : purchasedGoods.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            Long price = productPriceMap.getOrDefault(productId, 0L);
            totalPrice += price * quantity;
        }
        
        return totalPrice;
    }
}