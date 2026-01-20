package cn.fuguang.communicate.protocol.netty.constant;

/**
 * 命令ID常量类
 */
public class CommandIdConstants {
    
    // 基础命令
    public static final short HEARTBEAT = 0x00;           // 心跳包
    public static final short ACK = 0x01;                 // 确认响应
    
    // 设备控制命令
    public static final short OPEN_GATE = 0x10;           // 开柜命令
    public static final short CLOSE_GATE = 0x11;          // 关柜命令
    public static final short GET_GATE_STATUS = 0x12;     // 获取柜门状态
    
    // 设备状态上报命令
    public static final short REPORT_DEVICE_STATUS = 0x20; // 设备状态上报
    public static final short REPORT_WEIGHT = 0x21;       // 重量上报
    public static final short REPORT_TEMPERATURE = 0x22;  // 温度上报
    
    // 系统命令
    public static final short UPDATE_FIRMWARE = 0x30;     // 固件更新
    public static final short RESET_DEVICE = 0x31;        // 设备复位
    
    /**
     * 判断是否为响应命令
     * @param commandId 命令ID
     * @return true-响应命令，false-请求命令
     */
    public static boolean isResponse(short commandId) {
        // 响应命令的最高位为1
        return (commandId & 0x8000) != 0;
    }
    
    /**
     * 获取请求命令ID（清除最高位）
     * @param commandId 命令ID
     * @return 请求命令ID
     */
    public static short getRequestCommandId(short commandId) {
        return (short) (commandId & 0x7FFF);
    }
    
    /**
     * 获取响应命令ID（设置最高位为1）
     * @param commandId 请求命令ID
     * @return 响应命令ID
     */
    public static short getResponseCommandId(short commandId) {
        return (short) (commandId | 0x8000);
    }
}