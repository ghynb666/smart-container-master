package cn.fuguang.pojo.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class DeviceWeightBean implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 仓门id
     */
    private String gateId;

    /**
     * 设备重量信息
     */
    private Map<Integer, BigDecimal> weightMap;
}
