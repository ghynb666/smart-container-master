package cn.fuguang.enums;

public enum ActiveStatusEnum {

    /**
     * 生效
     */
    ACTIVE("生效"),
    /**
     * 失效
     */
    INACTIVE("失效");
    private final String desc;

    ActiveStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
