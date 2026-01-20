package cn.fuguang.pojo.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class WeightReportData implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 设备SN码
     */
    private String deviceSn;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 仓门状态 0:关门 1:开门
     */
    private String gateStatus;

    /**
     * 重量信息 map<货道ID, 重量值>
     */
    private Map<Integer, BigDecimal> weightMap;
}