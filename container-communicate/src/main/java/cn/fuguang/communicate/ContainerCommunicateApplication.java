package cn.fuguang.communicate;

import cn.fuguang.communicate.protocol.netty.server.NettyServer;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class ContainerCommunicateApplication implements CommandLineRunner {

    @Resource
    private NettyServer nettyServer;

    @Value("${server.socketKeNai.ip:127.0.0.1}")
    private String socketKeNaiIp;

    @Value("${server.socketKeNai.port:7000}")
    private String socketKeNaiPort;

    @Value("${server.socketYiNuo.ip:127.0.0.1}")
    private String socketYiNuoIp;

    @Value("${server.socketYiNuo.port:7001}")
    private String socketYiNuoPort;

    public static void main(String[] args) {
        SpringApplication.run(ContainerCommunicateApplication.class);
    }


    @Override
    public void run(String... args) {
        log.info("socketKeNaiIp : {}", socketKeNaiIp);
        log.info("socketKeNaiPort : {}", socketKeNaiPort);

        log.info("socketYiNuoIp : {}", socketYiNuoIp);
        log.info("socketYiNuoPort : {}", socketYiNuoPort);

        InetSocketAddress keNaiServerAddress = new InetSocketAddress(socketKeNaiIp, Integer.parseInt(socketKeNaiPort));
        InetSocketAddress yiNuoServerAddress = new InetSocketAddress(socketYiNuoIp, Integer.parseInt(socketYiNuoPort));
        List<ChannelFuture> channelFutureList = nettyServer.run(keNaiServerAddress,yiNuoServerAddress);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> nettyServer.destroy()));
        for (ChannelFuture future : channelFutureList) {
            future.channel().closeFuture().syncUninterruptibly();
        }

    }
}
