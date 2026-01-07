package cn.fuguang.enums;

public enum GateStatusEnum {

    INIT("初始化"),
    NORMAL("正常"),
    MAINTENANCE("维护中"),
    ABNORMAL("异常");
    private final String desc;

    GateStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
