package cn.fuguang.enums;

public enum OrderTypeEnum {


    SHOP_ORDER("SC", "商城订单"),

    VERITY_ORDER("VE", "验证码订单"),

    CALIBRATION("JZ", "校准订单"),

    REPLENISH_ORDER("BH","补货订单");
    private final String code;
    private final String desc;

    OrderTypeEnum(String desc,String code) {
        this.desc = desc;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
