package cn.fuguang.communicate.protocol.netty.codec;

import cn.fuguang.communicate.protocol.netty.entity.CustomProtocol;
import cn.fuguang.communicate.utils.CRC16Util;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义协议编码器
 */
@Slf4j
public class CustomProtocolEncoder extends MessageToByteEncoder<CustomProtocol> {
    
    @Override
    protected void encode(ChannelHandlerContext ctx, CustomProtocol protocol, ByteBuf out) throws Exception {
        try {
            // 1. 写入起始标志 (2字节)
            out.writeShort(protocol.getStartFlag());
            
            // 2. 写入版本号 (1字节)
            out.writeByte(protocol.getVersion());
            
            // 3. 写入设备类型 (1字节)
            out.writeByte(protocol.getDeviceType());
            
            // 4. 写入设备SN (16字节ASCII)
            byte[] deviceSnBytes = new byte[16];
            byte[] actualDeviceSnBytes = protocol.getDeviceSn().getBytes("ASCII");
            System.arraycopy(actualDeviceSnBytes, 0, deviceSnBytes, 0, Math.min(actualDeviceSnBytes.length, 16));
            out.writeBytes(deviceSnBytes);
            
            // 5. 写入命令ID (2字节)
            out.writeShort(protocol.getCommandId());
            
            // 6. 写入数据长度 (2字节)
            int dataLength = protocol.getData() != null ? protocol.getData().length : 0;
            out.writeShort(dataLength);
            
            // 7. 写入数据区 (N字节数组)
            if (dataLength > 0) {
                out.writeBytes(protocol.getData());
            }
            
            // 8. 计算并写入校验和 (2字节)
            // 提取从起始标志到数据区的所有字节用于计算CRC16
            int startIndex = out.writerIndex() - (1 + 1 + 16 + 2 + 2 + dataLength);
            ByteBuf crcBytes = out.slice(startIndex, 1 + 1 + 16 + 2 + 2 + dataLength);
            byte[] crcByteArray = new byte[crcBytes.readableBytes()];
            crcBytes.readBytes(crcByteArray);
            
            short checksum = CRC16Util.calculateCRC16(crcByteArray);
            out.writeShort(checksum);
            
            // 9. 写入结束标志 (2字节)
            out.writeShort(protocol.getEndFlag());
            
            log.debug("编码完成，设备SN: {}, 命令ID: {}, 数据长度: {}", protocol.getDeviceSn(), protocol.getCommandId(), dataLength);
        } catch (Exception e) {
            log.error("编码协议失败: {}", e.getMessage(), e);
            throw e;
        }
    }
}