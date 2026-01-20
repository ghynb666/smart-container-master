package cn.fuguang.communicate.protocol.netty.handle;

import cn.fuguang.communicate.protocol.netty.command.CommandHandler;
import cn.fuguang.communicate.protocol.netty.command.CommandHandlerFactory;
import cn.fuguang.communicate.protocol.netty.entity.CustomProtocol;
import cn.fuguang.communicate.protocol.netty.manager.ConnectionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自定义Netty消息处理器
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class CustomMessageHandler extends ChannelInboundHandlerAdapter {
    
    @Autowired
    private ConnectionManager connectionManager;
    
    @Autowired
    private CommandHandlerFactory commandHandlerFactory;
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("设备连接激活：Channel={}", ctx.channel().id());
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("设备连接关闭：Channel={}", ctx.channel().id());
        // 断开设备连接
        connectionManager.disconnect(ctx.channel());
        super.channelInactive(ctx);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof CustomProtocol) {
            CustomProtocol protocol = (CustomProtocol) msg;
            log.debug("收到设备消息：设备SN={}, 命令ID={}", protocol.getDeviceSn(), protocol.getCommandId());
            
            // 建立设备连接映射
            connectionManager.establishConnection(protocol.getDeviceSn(), ctx.channel());
            
            // 获取对应的命令处理器
            CommandHandler handler = commandHandlerFactory.getHandler(protocol.getCommandId());
            if (handler != null) {
                // 调用处理器处理命令
                handler.handle(ctx, protocol);
            } else {
                // 未找到对应的处理器
                log.warn("未找到命令处理器，命令ID：{}", protocol.getCommandId());
                // TODO: 返回未支持命令的响应
            }
        }
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // 180秒未收到设备消息，断开连接
                log.warn("设备心跳超时，断开连接：Channel={}", ctx.channel().id());
                connectionManager.disconnect(ctx.channel());
            }
        }
        super.userEventTriggered(ctx, evt);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理设备消息发生异常：Channel={}, 异常信息={}", ctx.channel().id(), cause.getMessage(), cause);
        // 断开设备连接
        connectionManager.disconnect(ctx.channel());
    }
}