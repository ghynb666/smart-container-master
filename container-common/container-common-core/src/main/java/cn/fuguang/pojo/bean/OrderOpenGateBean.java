package cn.fuguang.pojo.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Data
public class OrderOpenGateBean implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 仓门id
     */
    private String gateId;

    /**
     * 缓存创建时间
     */
    private Date createTime;

    /**
     * 设备重量信息
     */
    private Map<Integer, BigDecimal> weightMap;
}
