package cn.fuguang.enums;

public enum DeviceStatusEnum {

    INIT("初始化"),
    NORMAL("正常"),

    OFF_LINE("下线"),
    MAINTENANCE("维护中"),
    ABNORMAL("异常");
    private final String desc;

    DeviceStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
