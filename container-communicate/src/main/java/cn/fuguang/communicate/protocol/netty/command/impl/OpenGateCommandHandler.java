package cn.fuguang.communicate.protocol.netty.command.impl;

import cn.fuguang.communicate.protocol.netty.command.CommandHandler;
import cn.fuguang.communicate.protocol.netty.constant.CommandIdConstants;
import cn.fuguang.communicate.protocol.netty.entity.CustomProtocol;
import cn.fuguang.communicate.service.DeviceService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 开柜命令处理器
 */
@Component
@Slf4j
public class OpenGateCommandHandler implements CommandHandler {
    
    @Autowired
    private DeviceService deviceService;
    
    @Override
    public short getCommandId() {
        return CommandIdConstants.OPEN_GATE;
    }
    
    @Override
    public void handle(ChannelHandlerContext ctx, CustomProtocol protocol) {
        log.info("收到开柜命令：设备SN={}", protocol.getDeviceSn());
        
        // 解析开柜参数（示例：data[0]为柜门编号，data[1-16]为订单ID）
        int gateNumber = 1; // 默认开1号柜门
        String orderId = "test_order_" + System.currentTimeMillis(); // 默认订单ID
        
        if (protocol.getData() != null && protocol.getData().length > 0) {
            gateNumber = protocol.getData()[0] & 0xFF;
            
            // 解析订单ID（假设订单ID是16字节ASCII码）
            if (protocol.getData().length >= 17) {
                byte[] orderIdBytes = new byte[16];
                System.arraycopy(protocol.getData(), 1, orderIdBytes, 0, 16);
                orderId = new String(orderIdBytes).trim();
            }
        }
        
        log.info("执行开柜操作：设备SN={}, 柜门编号={}, 订单ID={}", protocol.getDeviceSn(), gateNumber, orderId);
        
        // 调用设备服务进行开柜
        deviceService.createOrderOpenGate(protocol.getDeviceSn(), orderId, gateNumber);
        
        // 回复开柜响应
        CustomProtocol response = new CustomProtocol();
        response.setVersion(protocol.getVersion());
        response.setDeviceType(protocol.getDeviceType());
        response.setDeviceSn(protocol.getDeviceSn());
        response.setCommandId(CommandIdConstants.getResponseCommandId(CommandIdConstants.OPEN_GATE));
        
        // 响应数据：0x00-成功，0x01-失败
        byte[] responseData = new byte[1];
        responseData[0] = 0x00; // 假设开柜成功
        response.setData(responseData);
        
        ctx.writeAndFlush(response);
        log.info("回复开柜命令：设备SN={}, 柜门编号={}, 结果={}", 
                protocol.getDeviceSn(), gateNumber, responseData[0] == 0x00 ? "成功" : "失败");
    }
}