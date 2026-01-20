package cn.fuguang.device.service.impl;

import cn.fuguang.device.mapper.DeviceInfoMapper;
import cn.fuguang.device.mapper.DeviceWeightRecordMapper;
import cn.fuguang.device.service.WeightRecordService;
import cn.fuguang.entity.DeviceInfoEntity;
import cn.fuguang.entity.DeviceWeightRecordEntity;
import cn.fuguang.pojo.bean.WeightStorageMessage;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 重量记录服务实现类
 */
@Service
@Slf4j
public class WeightRecordServiceImpl implements WeightRecordService {
    
    @Resource
    private DeviceWeightRecordMapper deviceWeightRecordMapper;
    
    @Resource
    private DeviceInfoMapper deviceInfoMapper;
    
    @Override
    public void processWeightStorage(WeightStorageMessage storageMessage) {
        log.info("开始处理重量数据存储，消息ID：{}", storageMessage.getMessageId());
        
        try {
            // 1. 获取设备信息，用于获取设备ID（如果需要）
            String deviceSn = storageMessage.getDeviceSn();
            DeviceInfoEntity deviceInfo = deviceInfoMapper.queryByDeviceSn(deviceSn);
            if (deviceInfo == null) {
                log.warn("设备信息不存在，设备SN：{}", deviceSn);
                // 可以选择继续处理，或者跳过
            }
            
            // 2. 解析重量数据，为每个货道创建重量记录
            String gateStatus = storageMessage.getGateStatus();
            Map<Integer, BigDecimal> weightMap = storageMessage.getWeightMap();
            
            List<DeviceWeightRecordEntity> recordList = new ArrayList<>();
            for (Map.Entry<Integer, BigDecimal> entry : weightMap.entrySet()) {
                Integer laneId = entry.getKey();
                BigDecimal weight = entry.getValue();
                
                // 创建重量记录实体
                DeviceWeightRecordEntity record = new DeviceWeightRecordEntity();
                record.setDeviceSn(deviceSn);
                // 这里假设货道ID格式为：设备ID_仓门ID_货道ID，实际项目中需要根据业务规则生成
                String laneUid = deviceSn + "_lane_" + laneId;
                record.setLaneUid(laneUid);
                record.setLaneWeight(weight);
                record.setGateStatus(gateStatus);
                // 简单判断重量是否稳定：开门状态下重量可能不稳定，关门状态下重量稳定
                String weightStatus = "1"; // 默认稳定
                if ("1".equals(gateStatus)) {
                    weightStatus = "0"; // 开门状态下不稳定
                }
                record.setWeightStatus(weightStatus);
                record.setCreateDate(new Date());
                
                recordList.add(record);
            }
            
            // 3. 批量插入重量记录
            if (!recordList.isEmpty()) {
                int result = deviceWeightRecordMapper.batchInsert(recordList);
                log.info("重量数据存储完成，消息ID：{}，设备SN：{}，插入记录数：{}", 
                        storageMessage.getMessageId(), deviceSn, result);
            }
            
        } catch (Exception e) {
            log.error("处理重量数据存储异常，消息ID：{}，消息内容：{}", 
                    storageMessage.getMessageId(), JSONObject.toJSONString(storageMessage), e);
            // 可以选择抛出异常，让MQ进行重试
            throw e;
        }
    }
    
    @Override
    public void batchProcessWeightStorage(List<WeightStorageMessage> storageMessages) {
        log.info("开始批量处理重量数据存储，消息数量：{}", storageMessages.size());
        
        for (WeightStorageMessage storageMessage : storageMessages) {
            try {
                processWeightStorage(storageMessage);
            } catch (Exception e) {
                log.error("批量处理单个重量数据存储异常，消息ID：{}", storageMessage.getMessageId(), e);
                // 单个消息处理失败不影响其他消息处理
            }
        }
        
        log.info("批量处理重量数据存储完成");
    }
}