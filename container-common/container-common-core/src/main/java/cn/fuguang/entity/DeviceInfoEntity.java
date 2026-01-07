package cn.fuguang.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DeviceInfoEntity {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 设备Sn
     */
    private String deviceSn;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备状态
     */
    private String deviceStatus;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备加热开关
     */
    private String heatingSwitchStatus;

    /**
     * 加热阈值
     */
    private Integer heatingThreshold;

    /**
     * 关闭加热阈值温度
     */
    private Integer closeHeatingThreshold;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String county;

    /**
     * 社区
     */
    private String community;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 设备描述
     */
    private String deviceDesc;

    /**
     * 灯光开启时间
     */
    private String lgtOnTime;

    /**
     * 高德经纬度
     */
    private String addrLocationGd;

    /**
     * 腾讯经纬度
     */
    private String addrLocationTx;

    /**
     * 设备版本号
     */
    private String deviceVersion;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remarks;


    /**
     * 之前的状态 不是数据库字段
     */
    private String oldDeviceStatus;

}
