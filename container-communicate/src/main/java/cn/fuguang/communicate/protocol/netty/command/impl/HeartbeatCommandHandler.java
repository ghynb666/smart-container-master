package cn.fuguang.communicate.protocol.netty.command.impl;

import cn.fuguang.communicate.protocol.netty.command.CommandHandler;
import cn.fuguang.communicate.protocol.netty.constant.CommandIdConstants;
import cn.fuguang.communicate.protocol.netty.entity.CustomProtocol;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 心跳命令处理器
 */
@Component
@Slf4j
public class HeartbeatCommandHandler implements CommandHandler {
    
    @Override
    public short getCommandId() {
        return CommandIdConstants.HEARTBEAT;
    }
    
    @Override
    public void handle(ChannelHandlerContext ctx, CustomProtocol protocol) {
        log.debug("收到心跳包：设备SN={}", protocol.getDeviceSn());
        
        // 回复心跳响应
        CustomProtocol response = new CustomProtocol();
        response.setVersion(protocol.getVersion());
        response.setDeviceType(protocol.getDeviceType());
        response.setDeviceSn(protocol.getDeviceSn());
        response.setCommandId(CommandIdConstants.getResponseCommandId(CommandIdConstants.HEARTBEAT));
        response.setData(new byte[0]);
        
        ctx.writeAndFlush(response);
        log.debug("回复心跳包：设备SN={}", protocol.getDeviceSn());
    }
}