package cn.fuguang.enums;

public enum ValidateSmsType {

    /**
     * 生效
     */
    REGISTER("注册用户验证", "【自动售卖柜】验证码${code}，此验证码用于注册用户验证，有效期至${expireTime}，请在10分钟内完成验证；为保障帐号安全请勿转发验证码给他人，有问题请致电110");
    private final String desc;
    private final String template;

    ValidateSmsType(String desc, String template) {
        this.desc = desc;
        this.template = template;
    }

    public String getDesc() {
        return desc;
    }

    public String getTemplate() {
        return template;
    }
}
