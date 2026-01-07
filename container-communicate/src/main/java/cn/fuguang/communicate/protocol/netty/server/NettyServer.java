package cn.fuguang.communicate.protocol.netty.server;

import cn.fuguang.communicate.protocol.netty.initializer.KeNaiServerChannelInitializer;
import cn.fuguang.communicate.protocol.netty.initializer.YiNuoServerChannelInitializer;
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
    private final EventLoopGroup yiNuoBossGroup = new NioEventLoopGroup();
    private final EventLoopGroup yiNuoWorkerGroup = new NioEventLoopGroup();

    private final EventLoopGroup keNaiBossGroup = new NioEventLoopGroup();

    private final EventLoopGroup keNaiWorkerGroup = new NioEventLoopGroup();

    private Channel yiNuoChannel;

    private Channel keNaiChannel;

    public List<ChannelFuture> run(InetSocketAddress keNaiAddress, InetSocketAddress yiNuoAddress) {

        ChannelFuture kaiNaiChannelFuture = null;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(keNaiBossGroup, keNaiWorkerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new KeNaiServerChannelInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            kaiNaiChannelFuture = b.bind(keNaiAddress).syncUninterruptibly();
            keNaiChannel = kaiNaiChannelFuture.channel();
        } catch (Exception e) {
            log.error("Netty start error:", e);
        } finally {
            if (kaiNaiChannelFuture != null && kaiNaiChannelFuture.isSuccess()) {
                log.info("Netty server listening " + keNaiAddress.getHostName() + " on port " + keNaiAddress.getPort() + " and ready for connections...");
            } else {
                log.error("Netty server start up Error!");
            }
        }

        ChannelFuture yiNuoChannelFuture = null;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(yiNuoBossGroup, yiNuoWorkerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new YiNuoServerChannelInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            yiNuoChannelFuture = b.bind(yiNuoAddress).syncUninterruptibly();
            yiNuoChannel = yiNuoChannelFuture.channel();
        } catch (Exception e) {
            log.error("Netty start error:", e);
        } finally {
            if (yiNuoChannelFuture != null && yiNuoChannelFuture.isSuccess()) {
                log.info("Netty server listening " + yiNuoAddress.getHostName() + " on port " + yiNuoAddress.getPort() + " and ready for connections...");
            } else {
                log.error("Netty server start up Error!");
            }
        }

        return Arrays.asList(kaiNaiChannelFuture,yiNuoChannelFuture);
    }


    public void destroy() {
        log.info("Shutdown Netty Server...");
        if (keNaiChannel != null) {
            keNaiChannel.close();
        }
        if (yiNuoChannel != null) {
            yiNuoChannel.close();
        }
        keNaiBossGroup.shutdownGracefully();
        keNaiWorkerGroup.shutdownGracefully();
        yiNuoBossGroup.shutdownGracefully();
        yiNuoWorkerGroup.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
    }

}
