package cn.fuguang.communicate.protocol.netty.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangjin
 */
@Slf4j
public class KeNaiStrategySelectHandler extends ChannelInboundHandlerAdapter {


    /**
     * 超时处理
     * 如果5秒没有接受客户端的心跳，就触发;
     * 如果超过两次，则直接关闭;
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            //如果读通道处于空闲状态，说明没有接收到心跳命令
            if (IdleState.READER_IDLE.equals(event.state())) {
                log.info("通道关闭");
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, obj);
        }
    }

    /**
     * 业务逻辑处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("ctx : {}", ctx);
//        log.info("channel : {}", ctx.channel());
//        log.info("服务端接受的消息:" + msg);
//        byte[] a = (byte[]) msg;
//
//        log.info("hex Str is {}",CommandUtil.bytesToHexString(a));
//
//        byte[] command = new byte[1];
//        System.arraycopy(a, 19, command, 0, 1);
//        log.info("command : 0x{}", CommandUtil.bytesToHexString(command));
//
//        Integer messsageTypeValue = Integer.parseInt(CommandUtil.bytesToHexString(command), 16);
//        NettyStrategyKeNaiHandler strategyHandler = NettyStrategyKeNaiContext.getStrategy(messsageTypeValue);
//        if (null != strategyHandler) {
//            strategyHandler.handler(ctx, msg);
//        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
//        NettySocketHolder.remove((NioSocketChannel) ctx.channel());
//        log.info("channelMap : {}", NettySocketHolder.getChannelMap());
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught : {}", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}
