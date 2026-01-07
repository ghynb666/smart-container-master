package cn.fuguang.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.text.MessageFormat;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContainerException extends RuntimeException {
    private static final long serialVersionUID = -1L;
    protected String message;
    protected String defineCode;

    public static final ContainerException PARAMS_ERROR = new ContainerException("100001","参数异常");

    public static final ContainerException CUSTOMER_UNPAID_ERROR = new ContainerException("100002","客户有未支付的订单");

    public static final ContainerException BLACK_CUSTOMER_ERROR = new ContainerException("100003","黑名单客户");

    public static final ContainerException DATE_NOT_EXIST_ERROR = new ContainerException("100004","数据不存在");

    public static final ContainerException DEVICE_STATUS_ERROR = new ContainerException("100005","设备状态异常");

    public static final ContainerException REDIS_LOCK_ERROR = new ContainerException("100006","获取redis锁失败");

    public static final ContainerException DATABASE_INERT_ERROR = new ContainerException("100007","插入数据库异常");

    public static final ContainerException REMOTE_CALL_ERROR = new ContainerException("100008","远程调用异常");

    public static final ContainerException DATABASE_UPDATE_ERROR = new ContainerException("100009","更新数据库异常");

    public static final ContainerException DATABASE_QUERY_ERROR = new ContainerException("100009","查询数据库异常");

    public static final ContainerException ROCKETMQ_SEND_ERROR = new ContainerException("100010","MQ消息发送异常");

    public static final ContainerException VERIFY_ERROR = new ContainerException("100011","验签异常");

    public static final ContainerException REDIS_ERROR = new ContainerException("100012","redis异常");

    public static final ContainerException DEVICE_CACHE_ERROR = new ContainerException("100013","设备缓存未找到");

    public static final ContainerException JSON_CONVERT_ERROR = new ContainerException("100014","json转化异常");

    public static final ContainerException ROCKETMQ_CONSUMER_ERROR = new ContainerException("100015","MQ消息消费异常");

    public static final ContainerException CUSTOMER_AUTH_ERROR = new ContainerException("100016","用户认证异常");

    public static final ContainerException SYSTEM_ERROR = new ContainerException("199999","系统异常");


    public ContainerException(String defineCode, String message, Object... args) {
        super(defineCode);
        this.setMessage(message,args);
    }

    public void setMessage(String message, Object... args) {
        this.message = MessageFormat.format(message, args);
    }

    public ContainerException newInstance(String message,Object... args) {
        return new ContainerException(defineCode, message,args);
    }

}
