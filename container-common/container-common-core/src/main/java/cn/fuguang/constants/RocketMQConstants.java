package cn.fuguang.constants;

public class RocketMQConstants {


    /**
     * 创建订单延迟消息topic
     */
    public static final String CREATE_ORDER_DELAY_TOPIC = "create_order_delay_topic";

    /**
     * 通信服务事件消息topic
     */
    public static final String COMMUNICATE_EVENT_TOPIC = "communicate_event_topic";

    /**
     * 通信服务事件消息consumer
     */
    public static final String COMMUNICATE_EVENT_CONSUMER = "communicate_event_consumer";

    /**
     * 优惠券秒杀消息topic
     */
    public static final String SECKILL_COUPON_TOPIC = "seckill_coupon_topic";

    /**
     * 优惠券秒杀消息consumer
     */
    public static final String SECKILL_COUPON_CONSUMER = "seckill_coupon_consumer";

    /**
     * 重量数据落库消息topic
     */
    public static final String WEIGHT_DATA_STORAGE_TOPIC = "weight_data_storage_topic";

    /**
     * 重量数据落库消息consumer
     */
    public static final String WEIGHT_DATA_STORAGE_CONSUMER = "weight_data_storage_consumer";
}
