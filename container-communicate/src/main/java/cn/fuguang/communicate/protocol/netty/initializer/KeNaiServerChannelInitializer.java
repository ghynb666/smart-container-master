package cn.fuguang.communicate.protocol.netty.initializer;

import cn.fuguang.communicate.protocol.netty.handle.KeNaiStrategySelectHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class KeNaiServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(new IdleStateHandler(180, 0, 0, TimeUnit.SECONDS));
        // 解码编码
        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 2, 2, -4, 0));
        socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        socketChannel.pipeline().addLast(new ByteArrayDecoder());
        socketChannel.pipeline().addLast(new ByteArrayEncoder());
        socketChannel.pipeline().addLast(new KeNaiStrategySelectHandler());
    }
}
