package cn.fuguang.device.service.impl;

import cn.fuguang.common.redis.service.RedisService;
import cn.fuguang.constants.RedisConstants;
import cn.fuguang.device.mapper.DeviceInfoMapper;
import cn.fuguang.device.mapper.DeviceWeightRecordMapper;
import cn.fuguang.device.service.DeviceWeightService;
import cn.fuguang.entity.DeviceInfoEntity;
import cn.fuguang.entity.DeviceWeightRecordEntity;
import cn.fuguang.pojo.bean.DeviceWeightBean;
import cn.fuguang.pojo.bean.WeightReportData;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 设备重量数据服务实现类
 */
@Service
@Slf4j
public class DeviceWeightServiceImpl implements DeviceWeightService {
    
    @Resource
    private RedisService redisService;
    
    @Resource
    private DeviceInfoMapper deviceInfoMapper;
    
    @Resource
    private DeviceWeightRecordMapper deviceWeightRecordMapper;
    
    @Override
    public DeviceWeightBean getCurrentWeight(String deviceSn) {
        log.info("查询设备当前重量信息，设备SN：{}", deviceSn);
        
        try {
            // 从Redis中获取当前重量信息
            String gateStatus = (String) redisService.hget(RedisConstants.WEIGHT_INFO, deviceSn + ":gateStatus");
            String weightMapStr = (String) redisService.hget(RedisConstants.WEIGHT_INFO, deviceSn + ":weightMap");
            
            if (gateStatus == null || weightMapStr == null) {
                log.warn("设备当前重量信息不存在，设备SN：{}", deviceSn);
                return null;
            }
            
            // 解析重量信息
            Map<Integer, BigDecimal> weightMap = JSONObject.parseObject(weightMapStr, Map.class);
            
            // 获取设备ID
            DeviceInfoEntity deviceInfo = deviceInfoMapper.queryByDeviceSn(deviceSn);
            String deviceId = deviceInfo != null ? deviceInfo.getDeviceId() : null;
            
            // 构建DeviceWeightBean
            DeviceWeightBean deviceWeightBean = new DeviceWeightBean();
            deviceWeightBean.setDeviceId(deviceId);
            deviceWeightBean.setDeviceSn(deviceSn);
            // 这里默认仓门ID为1，实际项目中需要根据业务规则获取
            deviceWeightBean.setGateId("1");
            deviceWeightBean.setWeightMap(weightMap);
            
            return deviceWeightBean;
            
        } catch (Exception e) {
            log.error("查询设备当前重量信息异常，设备SN：{}", deviceSn, e);
            return null;
        }
    }
    
    @Override
    public List<DeviceWeightBean> getHistoryWeight(String deviceSn, String startTime, String endTime) {
        log.info("查询设备历史重量记录，设备SN：{}，开始时间：{}，结束时间：{}", deviceSn, startTime, endTime);
        
        try {
            // 从数据库中查询历史重量记录
            List<DeviceWeightRecordEntity> recordList = deviceWeightRecordMapper.queryByDeviceSn(deviceSn, startTime, endTime);
            
            if (recordList == null || recordList.isEmpty()) {
                log.warn("设备历史重量记录不存在，设备SN：{}", deviceSn);
                return new ArrayList<>();
            }
            
            // 按时间分组，构建DeviceWeightBean列表
            Map<Date, DeviceWeightBean> weightMapByTime = new LinkedHashMap<>();
            
            for (DeviceWeightRecordEntity record : recordList) {
                Date createDate = record.getCreateDate();
                String gateStatus = record.getGateStatus();
                
                // 获取或创建该时间点的DeviceWeightBean
                DeviceWeightBean weightBean = weightMapByTime.get(createDate);
                if (weightBean == null) {
                    weightBean = new DeviceWeightBean();
                    weightBean.setDeviceSn(deviceSn);
                    weightBean.setGateStatus(gateStatus);
                    weightBean.setWeightMap(new HashMap<>());
                    weightMapByTime.put(createDate, weightBean);
                }
                
                // 解析货道ID（假设货道ID格式为：设备SN_lane_货道号）
                String laneUid = record.getLaneUid();
                String[] laneUidParts = laneUid.split("_");
                if (laneUidParts.length >= 3) {
                    Integer laneId = Integer.parseInt(laneUidParts[2]);
                    weightBean.getWeightMap().put(laneId, record.getLaneWeight());
                }
            }
            
            return new ArrayList<>(weightMapByTime.values());
            
        } catch (Exception e) {
            log.error("查询设备历史重量记录异常，设备SN：{}", deviceSn, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Map<String, Object>> getLaneHistoryWeight(String deviceSn, Integer laneId, String startTime, String endTime) {
        log.info("查询特定货道的历史重量记录，设备SN：{}，货道ID：{}，开始时间：{}，结束时间：{}", deviceSn, laneId, startTime, endTime);
        
        try {
            // 从数据库中查询历史重量记录
            List<DeviceWeightRecordEntity> recordList = deviceWeightRecordMapper.queryByDeviceSn(deviceSn, startTime, endTime);
            
            if (recordList == null || recordList.isEmpty()) {
                log.warn("设备历史重量记录不存在，设备SN：{}", deviceSn);
                return new ArrayList<>();
            }
            
            // 过滤出指定货道的记录
            List<Map<String, Object>> laneHistoryList = new ArrayList<>();
            String targetLaneUidPrefix = deviceSn + "_lane_" + laneId;
            
            for (DeviceWeightRecordEntity record : recordList) {
                if (record.getLaneUid().startsWith(targetLaneUidPrefix)) {
                    Map<String, Object> laneHistory = new HashMap<>();
                    laneHistory.put("timestamp", record.getCreateDate().getTime());
                    laneHistory.put("weight", record.getLaneWeight());
                    laneHistory.put("gateStatus", record.getGateStatus());
                    laneHistory.put("weightStatus", record.getWeightStatus());
                    laneHistoryList.add(laneHistory);
                }
            }
            
            return laneHistoryList;
            
        } catch (Exception e) {
            log.error("查询特定货道的历史重量记录异常，设备SN：{}，货道ID：{}", deviceSn, laneId, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public void saveWeightToCache(WeightReportData weightReportData) {
        log.info("保存设备重量信息到缓存，设备SN：{}", weightReportData.getDeviceSn());
        
        try {
            // 构建Redis Hash值
            Map<String, Object> redisHashValue = new HashMap<>();
            redisHashValue.put("gateStatus", weightReportData.getGateStatus());
            redisHashValue.put("weightMap", weightReportData.getWeightMap());
            redisHashValue.put("lastUpdateTime", weightReportData.getTimestamp());
            
            // 存储到Redis：大key为WEIGHT_INFO，小key为设备SN
            String deviceSn = weightReportData.getDeviceSn();
            for (Map.Entry<String, Object> entry : redisHashValue.entrySet()) {
                redisService.hset(RedisConstants.WEIGHT_INFO, deviceSn + ":" + entry.getKey(), entry.getValue());
            }
            
            log.info("设备重量信息已保存到缓存，设备SN：{}", deviceSn);
            
        } catch (Exception e) {
            log.error("保存设备重量信息到缓存异常，设备SN：{}", weightReportData.getDeviceSn(), e);
        }
    }
    
    @Override
    public DeviceWeightBean getWeightFromCache(String deviceId, String gateId) {
        log.info("从缓存中获取设备重量信息，设备ID：{}，仓门ID：{}", deviceId, gateId);
        
        try {
            // 先获取设备SN
            DeviceInfoEntity deviceInfo = deviceInfoMapper.queryDeviceInfoById(deviceId);
            if (deviceInfo == null) {
                log.warn("设备信息不存在，设备ID：{}", deviceId);
                return null;
            }
            
            String deviceSn = deviceInfo.getDeviceSn();
            
            // 从Redis中获取当前重量信息
            String gateStatus = (String) redisService.hget(RedisConstants.WEIGHT_INFO, deviceSn + ":gateStatus");
            String weightMapStr = (String) redisService.hget(RedisConstants.WEIGHT_INFO, deviceSn + ":weightMap");
            
            if (gateStatus == null || weightMapStr == null) {
                log.warn("设备重量信息不存在，设备ID：{}，仓门ID：{}", deviceId, gateId);
                return null;
            }
            
            // 解析重量信息
            Map<Integer, BigDecimal> weightMap = JSONObject.parseObject(weightMapStr, Map.class);
            
            // 构建DeviceWeightBean
            DeviceWeightBean deviceWeightBean = new DeviceWeightBean();
            deviceWeightBean.setDeviceId(deviceId);
            deviceWeightBean.setDeviceSn(deviceSn);
            deviceWeightBean.setGateId(gateId);
            deviceWeightBean.setWeightMap(weightMap);
            
            return deviceWeightBean;
            
        } catch (Exception e) {
            log.error("从缓存中获取设备重量信息异常，设备ID：{}，仓门ID：{}", deviceId, gateId, e);
            return null;
        }
    }
    
    @Override
    public String getDeviceIdBySn(String deviceSn) {
        log.info("获取设备SN对应的设备ID，设备SN：{}", deviceSn);
        
        try {
            DeviceInfoEntity deviceInfo = deviceInfoMapper.queryByDeviceSn(deviceSn);
            return deviceInfo != null ? deviceInfo.getDeviceId() : null;
        } catch (Exception e) {
            log.error("获取设备SN对应的设备ID异常，设备SN：{}", deviceSn, e);
            return null;
        }
    }
}