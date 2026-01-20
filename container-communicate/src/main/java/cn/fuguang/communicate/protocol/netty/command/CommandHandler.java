package cn.fuguang.communicate.protocol.netty.command;

import cn.fuguang.communicate.protocol.netty.entity.CustomProtocol;
import io.netty.channel.ChannelHandlerContext;

/**
 * 命令处理器接口
 */
public interface CommandHandler {
    
    /**
     * 获取该处理器支持的命令ID
     * @return 命令ID
     */
    short getCommandId();
    
    /**
     * 处理命令
     * @param ctx ChannelHandlerContext
     * @param protocol 协议对象
     */
    void handle(ChannelHandlerContext ctx, CustomProtocol protocol);
}