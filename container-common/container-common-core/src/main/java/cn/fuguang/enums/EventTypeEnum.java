package cn.fuguang.enums;

import lombok.Getter;

@Getter
public enum EventTypeEnum {

    HEART("心跳"),
    CLOSE_GATE("关门");
    private final String desc;

    EventTypeEnum(String desc) {
        this.desc = desc;
    }

}
