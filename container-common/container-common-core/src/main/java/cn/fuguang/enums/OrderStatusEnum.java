package cn.fuguang.enums;

public enum OrderStatusEnum {

    INIT("初始化", "未完成") {
        @Override
        public OrderStatusEnum nextStatus(OrderStatusEnum inputStatus) {
            return inputStatus;
        }
    },

    PRE_AUTH_CREATE_ERROR("预授权创建失败", "未完成") {
        @Override
        public OrderStatusEnum nextStatus(OrderStatusEnum inputStatus) {
            //理论上预授权创建失败不会接收任何状态的改变
            return PRE_AUTH_CREATE_ERROR;
        }
    },

    PRE_AUTH_CREATE_SUCCESS("预授权创建成功", "未完成") {
        @Override
        public OrderStatusEnum nextStatus(OrderStatusEnum inputStatus) {
            return null;
        }
    },


    PRE_AUTH_FAIL("预授权失败", "未完成") {
        @Override
        public OrderStatusEnum nextStatus(OrderStatusEnum inputStatus) {
            //理论上预授权创建失败不会接收任何状态的改变，到时候在考虑极端场景
            return PRE_AUTH_FAIL;
        }
    },

    PRE_AUTH_SUCCESS("预授权成功", "未支付") {
        @Override
        public OrderStatusEnum nextStatus(OrderStatusEnum inputStatus) {
           
            return null;
        }
    },

    FULLY_PAY("已支付", "已支付") {
        @Override
        public OrderStatusEnum nextStatus(OrderStatusEnum inputStatus) {
            //已支付的订单无论收到什么样的状态都是已支付
            return FULLY_PAY;
        }
    },

    IN_PAYMENT("支付中", "未支付") {
        @Override
        public OrderStatusEnum nextStatus(OrderStatusEnum inputStatus) {

            return null;
        }
    },

    CANCEL("订单取消", "订单取消") {
        @Override
        public OrderStatusEnum nextStatus(OrderStatusEnum inputStatus) {
            //订单取消
            return CANCEL;
        }
    },

    EXCEPTION("订单异常", "未支付") {
        @Override
        public OrderStatusEnum nextStatus(OrderStatusEnum inputStatus) {
            return null;
        }
    };


    private final String innerDesc;
    private final String outerDesc;

    OrderStatusEnum(String innerDesc, String outerDesc) {
        this.innerDesc = innerDesc;
        this.outerDesc = outerDesc;
    }

    public String getInnerDesc() {
        return innerDesc;
    }

    public String getOuterDesc() {
        return outerDesc;
    }



    public abstract OrderStatusEnum nextStatus(OrderStatusEnum inputStatus);

    /**
     * 下一个状态
     */
    public static String nextStatus(String currentStatus, String inputStatus){
        if (currentStatus == null){
            return OrderStatusEnum.valueOf(inputStatus).name();
        } else {
            return OrderStatusEnum.valueOf(currentStatus).nextStatus(OrderStatusEnum.valueOf(inputStatus)).name();
        }
    }
}
