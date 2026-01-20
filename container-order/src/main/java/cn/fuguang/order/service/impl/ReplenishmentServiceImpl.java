package cn.fuguang.order.service.impl;

import cn.fuguang.order.service.ReplenishmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 补货服务实现类
 */
@Service
@Slf4j
public class ReplenishmentServiceImpl implements ReplenishmentService {

    // 模拟补货任务数据，key为任务ID，value为补货任务详情
    private static final Map<String, Map<String, Object>> REPLENISHMENT_TASKS = new ConcurrentHashMap<>();
    
    // 商品补货阈值，key为商品ID，value为补货阈值
    private static final Map<String, Integer> REPLENISHMENT_THRESHOLD = new HashMap<>();
    
    // 商品名称映射，key为商品ID，value为商品名称
    private static final Map<String, String> PRODUCT_NAME_MAP = new HashMap<>();
    
    static {
        // 初始化补货阈值（当库存低于此值时触发补货）
        REPLENISHMENT_THRESHOLD.put("PROD001", 3); // 可乐 低于3瓶触发补货
        REPLENISHMENT_THRESHOLD.put("PROD002", 5); // 矿泉水 低于5瓶触发补货
        REPLENISHMENT_THRESHOLD.put("PROD003", 4); // 饼干 低于4包触发补货
        REPLENISHMENT_THRESHOLD.put("PROD004", 2); // 薯片 低于2包触发补货
        REPLENISHMENT_THRESHOLD.put("PROD005", 3); // 方便面 低于3桶触发补货
        
        // 初始化商品名称映射
        PRODUCT_NAME_MAP.put("PROD001", "可乐");
        PRODUCT_NAME_MAP.put("PROD002", "矿泉水");
        PRODUCT_NAME_MAP.put("PROD003", "饼干");
        PRODUCT_NAME_MAP.put("PROD004", "薯片");
        PRODUCT_NAME_MAP.put("PROD005", "方便面");
    }

    @Override
    public boolean checkAndTriggerReplenishment(String deviceId, String productId, int currentInventory) {
        log.info("检查库存并触发补货，设备ID：{}商品ID：{}当前库存：{}", deviceId, productId, currentInventory);
        
        // 获取补货阈值
        int threshold = REPLENISHMENT_THRESHOLD.getOrDefault(productId, 5);
        
        // 检查是否需要补货
        if (currentInventory > threshold) {
            log.info("库存充足，不需要补货，设备ID：{}商品ID：{}当前库存：{}阈值：{}", 
                    deviceId, productId, currentInventory, threshold);
            return false;
        }
        
        // 检查是否已存在相同的未完成补货任务
        for (Map<String, Object> task : REPLENISHMENT_TASKS.values()) {
            if (deviceId.equals(task.get("deviceId")) && 
                productId.equals(task.get("productId")) && 
                "PENDING".equals(task.get("status"))) {
                log.info("已存在相同的未完成补货任务，无需重复触发，设备ID：{}商品ID：{}", deviceId, productId);
                return false;
            }
        }
        
        // 触发补货，创建补货任务
        Map<String, Object> task = new HashMap<>();
        String taskId = "REPLEN" + System.currentTimeMillis();
        
        task.put("taskId", taskId);
        task.put("deviceId", deviceId);
        task.put("productId", productId);
        task.put("productName", PRODUCT_NAME_MAP.getOrDefault(productId, productId));
        task.put("currentInventory", currentInventory);
        task.put("threshold", threshold);
        task.put("requestQuantity", 20); // 每次请求补货20个
        task.put("createTime", System.currentTimeMillis());
        task.put("status", "PENDING"); // PENDING-待处理，PROCESSING-处理中，COMPLETED-已完成，CANCELLED-已取消
        
        // 保存补货任务
        REPLENISHMENT_TASKS.put(taskId, task);
        
        log.info("补货任务创建成功，任务ID：{}设备ID：{}商品ID：{}当前库存：{}阈值：{}", 
                taskId, deviceId, productId, currentInventory, threshold);
        
        return true;
    }

    @Override
    public List<Map<String, Object>> queryReplenishmentTasks(String deviceId, String status) {
        log.info("查询补货任务，设备ID：{}状态：{}", deviceId, status);
        
        // 筛选补货任务
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> task : REPLENISHMENT_TASKS.values()) {
            boolean matchDevice = (deviceId == null || deviceId.equals(task.get("deviceId")));
            boolean matchStatus = (status == null || status.equals(task.get("status")));
            
            if (matchDevice && matchStatus) {
                result.add(task);
            }
        }
        
        log.info("查询补货任务成功，设备ID：{}状态：{}查询到{}条记录", deviceId, status, result.size());
        
        return result;
    }

    @Override
    public boolean updateReplenishmentTaskStatus(String taskId, String status) {
        log.info("更新补货任务状态，任务ID：{}新状态：{}", taskId, status);
        
        // 检查任务是否存在
        Map<String, Object> task = REPLENISHMENT_TASKS.get(taskId);
        if (task == null) {
            log.error("补货任务不存在，任务ID：{}", taskId);
            return false;
        }
        
        // 更新任务状态
        task.put("status", status);
        task.put("updateTime", System.currentTimeMillis());
        
        REPLENISHMENT_TASKS.put(taskId, task);
        
        log.info("补货任务状态更新成功，任务ID：{}旧状态：{}新状态：{}", 
                taskId, task.get("status"), status);
        
        return true;
    }
}