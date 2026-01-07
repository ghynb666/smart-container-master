package cn.fuguang.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GateInfoEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 仓门Id
     */
    private String gateId;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 设备SN
     */
    private String deviceSn;

    /**
     * 仓门名称
     */
    private String gateName;

    /**
     * 仓门代码
     */
    private String gateCode;

    /**
     * 仓门序号
     */
    private Integer sort;

    /**
     * 仓门状态
     */
    private String gateStatus;

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

}
