package cn.fuguang.device.service;

import cn.fuguang.pojo.bean.WeightStorageMessage;

/**
 * 重量记录服务接口
 */
public interface WeightRecordService {
    
    /**
     * 处理重量数据存储
     * @param storageMessage 重量存储消息
     */
    void processWeightStorage(WeightStorageMessage storageMessage);
    
    /**
     * 批量处理重量数据存储
     * @param storageMessages 重量存储消息列表
     */
    void batchProcessWeightStorage(java.util.List<WeightStorageMessage> storageMessages);
}