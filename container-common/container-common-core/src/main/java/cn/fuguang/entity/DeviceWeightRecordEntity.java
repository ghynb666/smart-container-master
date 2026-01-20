package cn.fuguang.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 设备重量记录实体类
 */
@Data
public class DeviceWeightRecordEntity {
    
    /**
     * 主键id
     */
    private Integer id;
    
    /**
     * 设备SN码
     */
    private String deviceSn;
    
    /**
     * 货道id
     */
    private String laneUid;
    
    /**
     * 货道重量
     */
    private BigDecimal laneWeight;
    
    /**
     * 仓门状态 0关门 1开门
     */
    private String gateStatus;
    
    /**
     * 重量是否稳定 0否1是
     */
    private String weightStatus;
    
    /**
     * 创建时间
     */
    private Date createDate;
}