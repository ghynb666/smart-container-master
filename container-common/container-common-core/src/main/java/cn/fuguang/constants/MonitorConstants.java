package cn.fuguang.constants;

public class MonitorConstants {

    /**
     * 创建订单发送延迟消息报警
     */
    public static final String CREATE_ORDER_SEND_ERROR = "create_order_send_error";

    /**
     * 验证签名失败报警
     */
    public static final String SIGN_VERIFY_ERROR = "sign_verify_error";

    /**
     * redisson释放分布式锁异常报警
     */
    public static final String REDISSON_UN_LOCK_ERROR = "redisson_un_lock_error";

    /**
     * 设备重量缓存不存在报警
     */
    public static final String DEVICE_WEIGHT_CACHE_ERROR = "device_weight_cache_error";

    /**
     * 设备心跳时间大于最大间隔报警
     */
    public static final String DEVICE_HEART_GT_MAX_INTERVAL_ERROR = "device_heart_gt_max_interval_error";

    /**
     * 设备心跳网络异常报警
     */
    public static final String DEVICE_HEART_SERVICE_NETWORK_ERROR = "device_heart_service_network_error";
}
