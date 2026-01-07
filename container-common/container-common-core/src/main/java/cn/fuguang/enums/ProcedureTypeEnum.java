package cn.fuguang.enums;

public enum ProcedureTypeEnum {


    WECHAT("微信小程序"),

    ALI("支付宝小程序");
    private final String desc;

    ProcedureTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
