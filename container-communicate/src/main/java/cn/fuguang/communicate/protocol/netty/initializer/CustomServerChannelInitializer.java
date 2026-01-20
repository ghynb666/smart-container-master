package cn.fuguang.communicate.protocol.netty.initializer;

import cn.fuguang.communicate.protocol.netty.codec.CustomProtocolDecoder;
import cn.fuguang.communicate.protocol.netty.codec.CustomProtocolEncoder;
import cn.fuguang.communicate.protocol.netty.handle.CustomMessageHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 自定义Netty服务器Channel初始化器
 */
@Slf4j
public class CustomServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        log.debug("初始化Channel: {}", socketChannel.id());
        
        socketChannel.pipeline()
                // 1. 空闲状态处理器：180秒未收到消息则触发空闲事件
                .addLast(new IdleStateHandler(180, 0, 0, TimeUnit.SECONDS))
                
                // 2. 日志处理器：调试用
                .addLast(new LoggingHandler(LogLevel.DEBUG))
                
                // 3. 基于长度字段的帧解码器：解决TCP粘包/拆包问题
                // 最大帧长度：1024字节
                // 长度字段偏移：2字节（跳过起始标志）
                // 长度字段长度：2字节
                // 长度调整值：-4（因为长度字段包括了自己和前面的2字节起始标志）
                // 剥离的前导字节数：4（起始标志2字节 + 长度字段2字节）
                .addLast(new LengthFieldBasedFrameDecoder(1024, 2, 2, -4, 4))
                
                // 4. 自定义协议编码器
                .addLast(new CustomProtocolEncoder())
                
                // 5. 自定义协议解码器
                .addLast(new CustomProtocolDecoder())
                
                // 6. 自定义消息处理器
                .addLast(new CustomMessageHandler());
    }
}