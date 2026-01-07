package cn.fuguang.pojo.event;

import lombok.Data;

import java.util.Date;

@Data
public class HeartEventBean {

    /**
     * 事件
     */
    private String event;

    /**
     * 心跳时间
     */
    private Date heartDate;

    /**
     * 设备sn
     */
    private String deviceSn;
}
