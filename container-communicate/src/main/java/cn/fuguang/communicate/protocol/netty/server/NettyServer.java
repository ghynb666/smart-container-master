package cn.fuguang.communicate.protocol.netty.server;

import cn.fuguang.communicate.protocol.netty.initializer.CustomServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class NettyServer {
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel;

    public List<ChannelFuture> run(InetSocketAddress keNaiAddress, InetSocketAddress yiNuoAddress) {
        // 注意：这里简化了实现，只启动一个Netty服务器，使用自定义Channel初始化器
        // 实际项目中可以根据需要启动多个服务器实例
        ChannelFuture channelFuture = null;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new CustomServerChannelInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            channelFuture = b.bind(keNaiAddress).syncUninterruptibly();
            channel = channelFuture.channel();
        } catch (Exception e) {
            log.error("Netty start error:", e);
        } finally {
            if (channelFuture != null && channelFuture.isSuccess()) {
                log.info("Netty server listening " + keNaiAddress.getHostName() + " on port " + keNaiAddress.getPort() + " and ready for connections...");
            } else {
                log.error("Netty server start up Error!");
            }
        }

        return Arrays.asList(channelFuture);
    }


    public void destroy() {
        log.info("Shutdown Netty Server...");
        if (channel != null) {
            channel.close();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
    }

}
