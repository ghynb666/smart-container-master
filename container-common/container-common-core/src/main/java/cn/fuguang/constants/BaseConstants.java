package cn.fuguang.constants;

public class BaseConstants {

    public static final String SUCCESS_CODE = "000000";
    public static final String ERROR_CODE = "999999";

    public static final String ERROR_MSG = "系统异常, 请稍后重试";
    public static final String DATE_PATTEN_STR = "yyyyMMddHHmmss";

    /**
     * 更新订单最大重试次数
     */
    public static final Integer UPDATE_ORDER_STATUS_MAX_RETRY = 3;

    /**
     * 发送消息超时时间
     */
    public static final Long ROCKETMQ_TIMEOUT_MILES = 3000L;

    /**
     * 设备下线最大心跳时间间隔
     */
    public static final Long DEVICE_OFF_LINE_MAX_TIME_INTERVAL = 5L;

    /**
     * 设备心跳间隔
     */
    public static final Long DEVICE_HEART_INTERVAL = 30L;

    /**
     * 心跳计数器阈值
     */
    public static final Double DEVICE_HEART_COUNT_THRESHOLD = 0.8;

    /**
     * 验证码有效期(分钟)
     */
    public static final Integer VERIFY_CODE_PERIOD = 10;


}
