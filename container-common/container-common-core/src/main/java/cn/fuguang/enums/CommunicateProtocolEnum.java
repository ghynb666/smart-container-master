package cn.fuguang.enums;

import lombok.Getter;

@Getter
public enum CommunicateProtocolEnum {

    NETTY("netty"),
    MQTT("mqtt");
    private final String desc;

    CommunicateProtocolEnum(String desc) {
        this.desc = desc;
    }
}
