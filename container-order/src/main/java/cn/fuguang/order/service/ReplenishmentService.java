package cn.fuguang.order.service;

import java.util.List;
import java.util.Map;

/**
 * 补货服务
 */
public interface ReplenishmentService {

    /**
     * 检查库存并触发补货
     * @param deviceId 设备ID
     * @param productId 商品ID
     * @param currentInventory 当前库存
     * @return 是否触发补货
     */
    boolean checkAndTriggerReplenishment(String deviceId, String productId, int currentInventory);
    
    /**
     * 查询补货任务列表
     * @param deviceId 设备ID（可选，为空则查询所有）
     * @param status 任务状态（可选，为空则查询所有）
     * @return 补货任务列表
     */
    List<Map<String, Object>> queryReplenishmentTasks(String deviceId, String status);
    
    /**
     * 更新补货任务状态
     * @param taskId 任务ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateReplenishmentTaskStatus(String taskId, String status);
}