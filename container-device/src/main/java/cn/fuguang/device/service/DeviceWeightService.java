package cn.fuguang.device.service;

import cn.fuguang.pojo.bean.DeviceWeightBean;
import cn.fuguang.pojo.bean.WeightReportData;

import java.util.List;
import java.util.Map;

/**
 * 设备重量数据服务
 */
public interface DeviceWeightService {
    
    /**
     * 查询设备当前重量信息
     * @param deviceSn 设备SN码
     * @return 设备重量信息
     */
    DeviceWeightBean getCurrentWeight(String deviceSn);
    
    /**
     * 查询设备历史重量记录
     * @param deviceSn 设备SN码
     * @param startTime 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime 结束时间（格式：yyyy-MM-dd HH:mm:ss）
     * @return 历史重量记录列表
     */
    List<DeviceWeightBean> getHistoryWeight(String deviceSn, String startTime, String endTime);
    
    /**
     * 查询特定货道的历史重量记录
     * @param deviceSn 设备SN码
     * @param laneId 货道ID
     * @param startTime 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime 结束时间（格式：yyyy-MM-dd HH:mm:ss）
     * @return 历史重量记录列表
     */
    List<Map<String, Object>> getLaneHistoryWeight(String deviceSn, Integer laneId, String startTime, String endTime);
    
    /**
     * 保存设备重量信息到缓存
     * @param weightReportData 重量上报数据
     */
    void saveWeightToCache(WeightReportData weightReportData);
    
    /**
     * 从缓存中获取设备重量信息
     * @param deviceId 设备ID
     * @param gateId 仓门ID
     * @return 设备重量信息
     */
    DeviceWeightBean getWeightFromCache(String deviceId, String gateId);
    
    /**
     * 获取设备SN对应的设备ID
     * @param deviceSn 设备SN码
     * @return 设备ID
     */
    String getDeviceIdBySn(String deviceSn);
}