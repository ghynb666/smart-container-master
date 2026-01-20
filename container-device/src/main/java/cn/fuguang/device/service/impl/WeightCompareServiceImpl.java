package cn.fuguang.device.service.impl;

import cn.fuguang.device.service.WeightCompareService;
import cn.fuguang.pojo.bean.DeviceWeightBean;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 重量对比与商品识别服务实现类
 */
@Service
@Slf4j
public class WeightCompareServiceImpl implements WeightCompareService {

    // 模拟商品重量映射，key为商品ID，value为商品重量（克）
    private static final Map<String, Integer> PRODUCT_WEIGHT_MAP = new HashMap<>();
    
    static {
        // 初始化模拟商品数据
        PRODUCT_WEIGHT_MAP.put("PROD001", 350); // 可乐 350g
        PRODUCT_WEIGHT_MAP.put("PROD002", 500); // 矿泉水 500g
        PRODUCT_WEIGHT_MAP.put("PROD003", 200); // 饼干 200g
        PRODUCT_WEIGHT_MAP.put("PROD004", 150); // 薯片 150g
        PRODUCT_WEIGHT_MAP.put("PROD005", 400); // 方便面 400g
    }

    @Override
    public Map<String, Integer> compareWeightAndIdentifyGoods(String deviceId, String gateId, DeviceWeightBean beforeWeight, DeviceWeightBean afterWeight) {
        log.info("开始重量对比与商品识别，设备ID：{}仓门ID：{}", deviceId, gateId);
        log.info("开门前重量：{}", JSONObject.toJSONString(beforeWeight));
        log.info("关门后重量：{}", JSONObject.toJSONString(afterWeight));
        
        // 模拟重量对比逻辑
        Map<Integer, BigDecimal> beforeWeightMap = beforeWeight.getWeightMap();
        Map<Integer, BigDecimal> afterWeightMap = afterWeight.getWeightMap();
        
        // 计算重量差
        Map<Integer, BigDecimal> weightDiffMap = new HashMap<>();
        for (Map.Entry<Integer, BigDecimal> entry : beforeWeightMap.entrySet()) {
            Integer laneId = entry.getKey();
            BigDecimal beforeW = entry.getValue();
            BigDecimal afterW = afterWeightMap.getOrDefault(laneId, BigDecimal.ZERO);
            
            // 计算重量差（开门前 - 关门后）
            BigDecimal diff = beforeW.subtract(afterW);
            weightDiffMap.put(laneId, diff);
        }
        
        log.info("重量差计算结果：{}", JSONObject.toJSONString(weightDiffMap));
        
        // 模拟商品识别逻辑（根据重量差识别商品）
        Map<String, Integer> purchasedGoods = new HashMap<>();
        
        // 遍历重量差，模拟识别商品
        for (Map.Entry<Integer, BigDecimal> entry : weightDiffMap.entrySet()) {
            Integer laneId = entry.getKey();
            BigDecimal diff = entry.getValue();
            
            // 如果重量差大于阈值（10g），说明有商品被取走
            if (diff.compareTo(new BigDecimal(10)) > 0) {
                // 模拟根据重量差识别商品
                String productId = identifyProductByWeight(diff);
                if (productId != null) {
                    purchasedGoods.put(productId, purchasedGoods.getOrDefault(productId, 0) + 1);
                    log.info("货道 {} 识别到商品 {}，重量差：{}", laneId, productId, diff);
                }
            }
        }
        
        log.info("商品识别结果：{}", JSONObject.toJSONString(purchasedGoods));
        return purchasedGoods;
    }

    @Override
    public Map<String, Integer> simulateGoodsIdentification(String deviceId, String gateId) {
        log.info("开始模拟商品识别，设备ID：{}仓门ID：{}", deviceId, gateId);
        
        // 模拟生成随机购买的商品
        Map<String, Integer> purchasedGoods = new HashMap<>();
        Random random = new Random();
        
        // 随机生成1-3件商品
        int productCount = random.nextInt(3) + 1;
        
        // 随机选择商品
        String[] productIds = PRODUCT_WEIGHT_MAP.keySet().toArray(new String[0]);
        for (int i = 0; i < productCount; i++) {
            String productId = productIds[random.nextInt(productIds.length)];
            purchasedGoods.put(productId, purchasedGoods.getOrDefault(productId, 0) + 1);
        }
        
        log.info("模拟商品识别结果：{}", JSONObject.toJSONString(purchasedGoods));
        return purchasedGoods;
    }
    
    /**
     * 根据重量差识别商品
     * @param weightDiff 重量差（克）
     * @return 商品ID
     */
    private String identifyProductByWeight(BigDecimal weightDiff) {
        // 将重量差转换为克（保留整数）
        int diffInGrams = weightDiff.intValue();
        
        // 模拟根据重量差查找最接近的商品
        String closestProduct = null;
        int minDiff = Integer.MAX_VALUE;
        
        for (Map.Entry<String, Integer> entry : PRODUCT_WEIGHT_MAP.entrySet()) {
            String productId = entry.getKey();
            int productWeight = entry.getValue();
            
            // 计算重量差的绝对值
            int currentDiff = Math.abs(diffInGrams - productWeight);
            
            // 如果当前差异更小，更新最接近的商品
            if (currentDiff < minDiff) {
                minDiff = currentDiff;
                closestProduct = productId;
            }
        }
        
        // 如果差异在合理范围内（±50g），返回识别结果
        return minDiff <= 50 ? closestProduct : null;
    }
}