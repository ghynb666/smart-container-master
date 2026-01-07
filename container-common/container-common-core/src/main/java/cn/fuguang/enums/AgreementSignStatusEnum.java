package cn.fuguang.enums;

public enum AgreementSignStatusEnum {


    NORMAL("正常"),

    UNSIGN("解约");
    private final String desc;

    AgreementSignStatusEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
