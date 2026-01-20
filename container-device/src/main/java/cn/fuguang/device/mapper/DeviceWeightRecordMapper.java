package cn.fuguang.device.mapper;

import cn.fuguang.entity.DeviceWeightRecordEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备重量记录Mapper
 */
public interface DeviceWeightRecordMapper {
    
    /**
     * 插入单条重量记录
     * @param record 重量记录实体
     * @return 影响行数
     */
    int insert(DeviceWeightRecordEntity record);
    
    /**
     * 批量插入重量记录
     * @param records 重量记录列表
     * @return 影响行数
     */
    int batchInsert(@Param("records") List<DeviceWeightRecordEntity> records);
    
    /**
     * 根据设备SN查询重量记录
     * @param deviceSn 设备SN
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 重量记录列表
     */
    List<DeviceWeightRecordEntity> queryByDeviceSn(@Param("deviceSn") String deviceSn, 
                                                  @Param("startTime") String startTime, 
                                                  @Param("endTime") String endTime);
}