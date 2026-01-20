package cn.fuguang.communicate.protocol.netty.entity;

import lombok.Data;

/**
 * 自定义TCP协议实体类
 */
@Data
public class CustomProtocol {
    // 起始标志 (2字节)：0x5A5A
    private static final short START_FLAG = (short) 0x5A5A;
    // 结束标志 (2字节)：0xAA55
    private static final short END_FLAG = (short) 0xAA55;
    
    // 版本号 (1字节)
    private byte version;
    // 设备类型 (1字节)：0x01-科耐设备，0x02-一诺设备
    private byte deviceType;
    // 设备SN (16字节ASCII)
    private String deviceSn;
    // 命令ID (2字节)
    private short commandId;
    // 数据区 (N字节数组)
    private byte[] data;
    // 校验和 (2字节，CRC16)
    private short checksum;
    
    // 获取完整协议长度（不包括起始和结束标志）
    public int getFullLength() {
        int dataLength = data != null ? data.length : 0;
        // 版本号(1) + 设备类型(1) + 设备SN(16) + 命令ID(2) + 数据长度(2) + 数据区(N) + 校验和(2)
        return 1 + 1 + 16 + 2 + 2 + dataLength + 2;
    }
    
    public short getStartFlag() {
        return START_FLAG;
    }
    
    public short getEndFlag() {
        return END_FLAG;
    }
}