package cn.fuguang.pojo.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class WeightStorageMessage implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 消息唯一ID
     */
    private String messageId;

    /**
     * 设备SN码
     */
    private String deviceSn;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 仓门ID
     */
    private String gateId;

    /**
     * 仓门状态 0:关门 1:开门
     */
    private String gateStatus;

    /**
     * 重量信息 map<货道ID, 重量值>
     */
    private Map<Integer, BigDecimal> weightMap;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 创建时间
     */
    private Long createTime;
}