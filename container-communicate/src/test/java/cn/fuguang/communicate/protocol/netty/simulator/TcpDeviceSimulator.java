package cn.fuguang.communicate.protocol.netty.simulator;

import cn.fuguang.communicate.protocol.netty.constant.CommandIdConstants;
import cn.fuguang.communicate.protocol.netty.entity.CustomProtocol;
import cn.fuguang.communicate.utils.CRC16Util;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * TCP设备模拟器
 */
@Slf4j
public class TcpDeviceSimulator {
    
    // 服务器地址
    private String serverHost;
    // 服务器端口
    private int serverPort;
    // 设备SN
    private String deviceSn;
    // 设备类型
    private byte deviceType;
    
    // Netty客户端相关
    private EventLoopGroup group;
    private Channel channel;
    
    /**
     * 构造函数
     * @param serverHost 服务器地址
     * @param serverPort 服务器端口
     * @param deviceSn 设备SN
     * @param deviceType 设备类型
     */
    public TcpDeviceSimulator(String serverHost, int serverPort, String deviceSn, byte deviceType) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.deviceSn = deviceSn;
        this.deviceType = deviceType;
    }
    
    /**
     * 启动设备模拟器
     */
    public void start() {
        group = new NioEventLoopGroup();
        
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    // 日志处理器
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    // 基于长度字段的帧解码器
                                    .addLast(new LengthFieldBasedFrameDecoder(1024, 2, 2, -4, 4))
                                    // 基于长度字段的帧编码器
                                    .addLast(new LengthFieldPrepender(2))
                                    // 字节数组解码器
                                    .addLast(new ByteArrayDecoder())
                                    // 字节数组编码器
                                    .addLast(new ByteArrayEncoder())
                                    // 自定义消息处理器
                                    .addLast(new TcpDeviceHandler());
                        }
                    });
            
            // 连接服务器
            ChannelFuture future = bootstrap.connect(serverHost, serverPort).sync();
            log.info("设备模拟器连接服务器成功：{}:{}, 设备SN={}", serverHost, serverPort, deviceSn);
            
            channel = future.channel();
            
            // 启动心跳线程
            startHeartbeatThread();
            
            // 等待连接关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("设备模拟器启动失败：{}", e.getMessage(), e);
        } finally {
            group.shutdownGracefully();
        }
    }
    
    /**
     * 启动心跳线程，定时发送心跳包
     */
    private void startHeartbeatThread() {
        new Thread(() -> {
            while (true) {
                try {
                    // 每60秒发送一次心跳包
                    TimeUnit.SECONDS.sleep(60);
                    
                    // 发送心跳包
                    sendHeartbeat();
                } catch (InterruptedException e) {
                    log.error("心跳线程异常：{}", e.getMessage(), e);
                    break;
                }
            }
        }, "heartbeat-thread").start();
    }
    
    /**
     * 发送心跳包
     */
    public void sendHeartbeat() {
        CustomProtocol protocol = new CustomProtocol();
        protocol.setVersion((byte) 0x01);
        protocol.setDeviceType(deviceType);
        protocol.setDeviceSn(deviceSn);
        protocol.setCommandId(CommandIdConstants.HEARTBEAT);
        protocol.setData(new byte[0]);
        
        sendMessage(protocol);
        log.info("发送心跳包：设备SN={}", deviceSn);
    }
    
    /**
     * 发送开柜命令
     * @param gateNumber 柜门编号
     */
    public void sendOpenGateCommand(int gateNumber) {
        CustomProtocol protocol = new CustomProtocol();
        protocol.setVersion((byte) 0x01);
        protocol.setDeviceType(deviceType);
        protocol.setDeviceSn(deviceSn);
        protocol.setCommandId(CommandIdConstants.OPEN_GATE);
        protocol.setData(new byte[]{(byte) gateNumber});
        
        sendMessage(protocol);
        log.info("发送开柜命令：设备SN={}, 柜门编号={}", deviceSn, gateNumber);
    }
    
    /**
     * 发送消息
     * @param protocol 协议对象
     */
    public void sendMessage(CustomProtocol protocol) {
        if (channel != null && channel.isActive()) {
            // 转换为字节数组并发送
            byte[] messageBytes = protocolToBytes(protocol);
            channel.writeAndFlush(messageBytes);
        } else {
            log.error("设备未连接，无法发送消息：设备SN={}", deviceSn);
        }
    }
    
    /**
     * 将协议对象转换为字节数组
     * @param protocol 协议对象
     * @return 字节数组
     */
    private byte[] protocolToBytes(CustomProtocol protocol) {
        // 计算数据长度
        int dataLength = protocol.getData() != null ? protocol.getData().length : 0;
        // 计算完整协议长度（包括起始和结束标志）
        int fullLength = 2 + 1 + 1 + 16 + 2 + 2 + dataLength + 2 + 2;
        
        byte[] bytes = new byte[fullLength];
        int index = 0;
        
        // 1. 写入起始标志 (2字节)：0x5A5A
        bytes[index++] = (byte) (protocol.getStartFlag() >> 8);
        bytes[index++] = (byte) (protocol.getStartFlag() & 0xFF);
        
        // 2. 写入版本号 (1字节)
        bytes[index++] = protocol.getVersion();
        
        // 3. 写入设备类型 (1字节)
        bytes[index++] = protocol.getDeviceType();
        
        // 4. 写入设备SN (16字节ASCII)
        byte[] deviceSnBytes = new byte[16];
        byte[] actualDeviceSnBytes = protocol.getDeviceSn().getBytes();
        System.arraycopy(actualDeviceSnBytes, 0, deviceSnBytes, 0, Math.min(actualDeviceSnBytes.length, 16));
        System.arraycopy(deviceSnBytes, 0, bytes, index, 16);
        index += 16;
        
        // 5. 写入命令ID (2字节)
        bytes[index++] = (byte) (protocol.getCommandId() >> 8);
        bytes[index++] = (byte) (protocol.getCommandId() & 0xFF);
        
        // 6. 写入数据长度 (2字节)
        bytes[index++] = (byte) (dataLength >> 8);
        bytes[index++] = (byte) (dataLength & 0xFF);
        
        // 7. 写入数据区 (N字节数组)
        if (dataLength > 0) {
            System.arraycopy(protocol.getData(), 0, bytes, index, dataLength);
            index += dataLength;
        }
        
        // 8. 计算并写入校验和 (2字节)
        // 提取从起始标志到数据区的所有字节用于计算CRC16
        byte[] crcData = new byte[fullLength - 4]; // 减去起始标志(2)和结束标志(2)
        System.arraycopy(bytes, 0, crcData, 0, crcData.length);
        short checksum = CRC16Util.calculateCRC16(crcData);
        bytes[index++] = (byte) (checksum >> 8);
        bytes[index++] = (byte) (checksum & 0xFF);
        
        // 9. 写入结束标志 (2字节)：0xAA55
        bytes[index++] = (byte) (protocol.getEndFlag() >> 8);
        bytes[index++] = (byte) (protocol.getEndFlag() & 0xFF);
        
        return bytes;
    }
    
    /**
     * 主函数，用于启动设备模拟器
     * @param args 参数
     */
    public static void main(String[] args) {
        // 默认参数
        String serverHost = "127.0.0.1";
        int serverPort = 7000;
        String deviceSn = "TEST_DEVICE_001";
        byte deviceType = 0x01; // 0x01-科耐设备，0x02-一诺设备
        
        // 解析命令行参数
        if (args.length >= 2) {
            serverHost = args[0];
            serverPort = Integer.parseInt(args[1]);
        }
        if (args.length >= 3) {
            deviceSn = args[2];
        }
        if (args.length >= 4) {
            deviceType = Byte.parseByte(args[3]);
        }
        
        // 创建并启动设备模拟器
        TcpDeviceSimulator simulator = new TcpDeviceSimulator(serverHost, serverPort, deviceSn, deviceType);
        
        // 启动模拟器
        new Thread(() -> simulator.start(), "simulator-thread").start();
        
        // 控制台交互
        Scanner scanner = new Scanner(System.in);
        log.info("TCP设备模拟器已启动，输入命令进行测试：");
        log.info("1. send-heartbeat - 发送心跳包");
        log.info("2. send-open-gate <gateNumber> - 发送开柜命令");
        log.info("3. exit - 退出模拟器");
        
        while (true) {
            String input = scanner.nextLine();
            String[] parts = input.split(" ");
            
            if (parts[0].equals("send-heartbeat")) {
                // 发送心跳包
                simulator.sendHeartbeat();
            } else if (parts[0].equals("send-open-gate") && parts.length >= 2) {
                // 发送开柜命令
                int gateNumber = Integer.parseInt(parts[1]);
                simulator.sendOpenGateCommand(gateNumber);
            } else if (parts[0].equals("exit")) {
                // 退出模拟器
                log.info("退出设备模拟器");
                System.exit(0);
            } else {
                log.info("无效命令，请重新输入");
            }
        }
    }
    
    /**
     * TCP设备处理器，用于处理服务器返回的消息
     */
    private class TcpDeviceHandler extends SimpleChannelInboundHandler<byte[]> {
        
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
            // 解析服务器返回的消息
            log.info("收到服务器消息：设备SN={}, 消息长度={}", deviceSn, msg.length);
            
            // TODO: 解析消息内容，处理服务器响应
            // 例如：检查是否为心跳响应、开柜响应等
        }
        
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("设备连接激活：设备SN={}", deviceSn);
            super.channelActive(ctx);
        }
        
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            log.info("设备连接关闭：设备SN={}", deviceSn);
            // 尝试重新连接
            reconnect();
            super.channelInactive(ctx);
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("设备通信异常：设备SN={}, 异常信息={}", deviceSn, cause.getMessage(), cause);
            ctx.close();
        }
        
        /**
         * 尝试重新连接
         */
        private void reconnect() {
            log.info("尝试重新连接服务器：设备SN={}", deviceSn);
            
            // 指数退避重连
            int retryInterval = 1;
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(retryInterval);
                    
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(group)
                            .channel(NioSocketChannel.class)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .option(ChannelOption.SO_KEEPALIVE, true)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) {
                                    ch.pipeline()
                                            .addLast(new LoggingHandler(LogLevel.DEBUG))
                                            .addLast(new LengthFieldBasedFrameDecoder(1024, 2, 2, -4, 4))
                                            .addLast(new LengthFieldPrepender(2))
                                            .addLast(new ByteArrayDecoder())
                                            .addLast(new ByteArrayEncoder())
                                            .addLast(new TcpDeviceHandler());
                                }
                            });
                    
                    ChannelFuture future = bootstrap.connect(serverHost, serverPort).sync();
                    log.info("设备重新连接服务器成功：设备SN={}", deviceSn);
                    channel = future.channel();
                    break;
                } catch (Exception e) {
                    log.error("设备重新连接服务器失败：设备SN={}, 异常信息={}", deviceSn, e.getMessage());
                    // 指数退避，最大64秒
                    retryInterval = Math.min(retryInterval * 2, 64);
                    log.info("{}秒后尝试重新连接：设备SN={}", retryInterval, deviceSn);
                }
            }
        }
    }
}