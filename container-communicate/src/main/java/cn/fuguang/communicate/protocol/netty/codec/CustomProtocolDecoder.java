package cn.fuguang.communicate.protocol.netty.codec;

import cn.fuguang.communicate.protocol.netty.entity.CustomProtocol;
import cn.fuguang.communicate.utils.CRC16Util;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 自定义协议解码器
 */
@Slf4j
public class CustomProtocolDecoder extends ByteToMessageDecoder {
    
    // 起始标志 (2字节)：0x5A5A
    private static final short START_FLAG = (short) 0x5A5A;
    // 结束标志 (2字节)：0xAA55
    private static final short END_FLAG = (short) 0xAA55;
    // 最小协议长度：起始标志(2) + 版本号(1) + 设备类型(1) + 设备SN(16) + 命令ID(2) + 数据长度(2) + 数据区(0) + 校验和(2) + 结束标志(2) = 28字节
    private static final int MIN_PROTOCOL_LENGTH = 28;
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            // 1. 确保至少有最小协议长度的数据可读
            if (in.readableBytes() < MIN_PROTOCOL_LENGTH) {
                return;
            }
            
            // 2. 查找起始标志
            int startIndex = findStartFlag(in);
            if (startIndex == -1) {
                // 未找到起始标志，跳过当前字节
                in.skipBytes(in.readableBytes());
                return;
            }
            
            // 3. 标记当前读取位置，以便重置
            in.markReaderIndex();
            
            // 4. 跳过起始标志
            in.skipBytes(2);
            
            // 5. 读取版本号
            byte version = in.readByte();
            
            // 6. 读取设备类型
            byte deviceType = in.readByte();
            
            // 7. 读取设备SN (16字节ASCII)
            byte[] deviceSnBytes = new byte[16];
            in.readBytes(deviceSnBytes);
            String deviceSn = new String(deviceSnBytes, StandardCharsets.US_ASCII).trim();
            
            // 8. 读取命令ID
            short commandId = in.readShort();
            
            // 9. 读取数据长度
            short dataLength = in.readShort();
            
            // 10. 确保有足够的数据可读
            if (in.readableBytes() < dataLength + 2 + 2) { // 数据区 + 校验和 + 结束标志
                in.resetReaderIndex();
                return;
            }
            
            // 11. 读取数据区
            byte[] data = new byte[dataLength];
            if (dataLength > 0) {
                in.readBytes(data);
            }
            
            // 12. 读取校验和
            short checksum = in.readShort();
            
            // 13. 读取结束标志
            short endFlag = in.readShort();
            
            // 14. 验证结束标志
            if (endFlag != END_FLAG) {
                log.warn("结束标志错误，预期: 0xAA55, 实际: 0x{}", Integer.toHexString(endFlag & 0xFFFF));
                in.resetReaderIndex();
                // 跳过一个字节，继续查找起始标志
                in.skipBytes(1);
                return;
            }
            
            // 15. 验证校验和
            // 重新提取从起始标志到数据区的所有字节用于验证CRC16
            in.resetReaderIndex();
            // 跳过起始标志，读取到数据区结束
            in.skipBytes(2);
            int crcDataLength = 1 + 1 + 16 + 2 + 2 + dataLength;
            byte[] crcData = new byte[crcDataLength];
            in.readBytes(crcData);
            
            if (!CRC16Util.verifyCRC16(crcData, checksum)) {
                log.warn("CRC16校验失败，设备SN: {}", deviceSn);
                // 跳过结束标志
                in.skipBytes(2);
                return;
            }
            
            // 16. 构造CustomProtocol对象
            CustomProtocol protocol = new CustomProtocol();
            protocol.setVersion(version);
            protocol.setDeviceType(deviceType);
            protocol.setDeviceSn(deviceSn);
            protocol.setCommandId(commandId);
            protocol.setData(data);
            protocol.setChecksum(checksum);
            
            // 17. 将解码后的协议对象添加到输出列表
            out.add(protocol);
            
            log.debug("解码完成，设备SN: {}, 命令ID: {}, 数据长度: {}", deviceSn, commandId, dataLength);
            
        } catch (Exception e) {
            log.error("解码协议失败: {}", e.getMessage(), e);
            // 发生异常时，跳过当前字节
            if (in.readableBytes() > 0) {
                in.skipBytes(1);
            }
        }
    }
    
    /**
     * 查找起始标志
     * @param in ByteBuf
     * @return 起始标志的位置，-1表示未找到
     */
    private int findStartFlag(ByteBuf in) {
        int readableBytes = in.readableBytes();
        if (readableBytes < 2) {
            return -1;
        }
        
        for (int i = 0; i <= readableBytes - 2; i++) {
            short flag = in.getShort(in.readerIndex() + i);
            if (flag == START_FLAG) {
                return i;
            }
        }
        return -1;
    }
}