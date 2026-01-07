package cn.fuguang.api.communicate.dto.req;

import cn.fuguang.enums.CommunicateProtocolEnum;
import cn.fuguang.enums.DeviceSupplierEnum;
import cn.fuguang.enums.EventTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Data
public class EventAcceptReqDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 事件
     */
    @NotNull(message = "event can not be null")
    private EventTypeEnum event;

    /**
     * 使用的通信协议
     */
    @NotNull(message = "protocolEnum can not be null")
    private CommunicateProtocolEnum protocolEnum;

    /**
     * 设备厂商
     */
    @NotNull(message = "protocolEnum can not be null")
    private DeviceSupplierEnum deviceSupplierEnum;

    /**
     * 事件对应参数
     */
    @NotNull(message = "params can not be null")
    private Map<String, Object> params;
}
