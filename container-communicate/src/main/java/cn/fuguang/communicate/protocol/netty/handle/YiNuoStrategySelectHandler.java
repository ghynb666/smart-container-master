package cn.fuguang.communicate.protocol.netty.handle;

import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class YiNuoStrategySelectHandler extends ChannelInboundHandlerAdapter {


    @Resource
    private ApplicationContext applicationContext;
    /**
     * 超时处理
     * 服务器下线机制在这，如果180秒没有读到任何信息，说明服务下线，做下线处理
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
//        log.info("触发userEventTriggered事件执行,obj:{}", JSONUtil.toJsonStr(obj));
//        if (obj instanceof IdleStateEvent event) {
//            if (IdleState.READER_IDLE.equals(event.state())) {  //如果读通道处于空闲状态，说明没有接收到心跳命令
//                log.info("通道关闭");
//                ctx.channel().close();
//            }
//        } else {
//            super.userEventTriggered(ctx, obj);
//        }
    }

    /**
     * 业务逻辑处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        log.info("服务端接受的消息:{},channelId:{}", msg, ctx.channel().id());
//        byte[] msgByteArray = (byte[]) msg;
//        byte[] command = new byte[1];
//        System.arraycopy(msgByteArray, 4, command, 0, 1);
//        log.info("command : 0x{}", HexUtil.encodeHexStr(command));
//        Integer messageTypeValue = HexUtil.hexToInt(HexUtil.encodeHexStr(command));
//        log.info("messageTypeValue:{}",messageTypeValue);
//        String beanName = YiNuoPullMessageEnum.getNameByValue(messageTypeValue);
//        if (StrUtil.isBlank(beanName)) {
//            log.info("找不到设备发送的消息类型: 0x{}", HexUtil.encodeHexStr(command));
//        }
//        NettyStrategyYiNuoHandler strategyHandler = applicationContext.getBean(beanName, NettyStrategyYiNuoHandler.class);
//        strategyHandler.handler(ctx, msgByteArray);

    }

    /**
     * 连接断开，做下线处理
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("触发channelInactive事件执行,ctx:{}", JSONUtil.toJsonStr(ctx));
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        log.info("触发exceptionCaught事件执行,Throwable:{}", JSONUtil.toJsonStr(cause));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        log.info("触发channelReadComplete事件执行,channelId:{}", ctx.channel().id());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        log.info("触发channelRegistered事件执行,channelId:{}", ctx.channel().id());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.info("触发channelUnregistered事件执行,channelId:{}", ctx.channel().id());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("触发channelActive事件执行,ctx:{}", JSONUtil.toJsonStr(ctx));
    }
}
