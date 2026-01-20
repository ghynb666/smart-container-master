package cn.fuguang.communicate.protocol.netty.command;

import cn.fuguang.communicate.protocol.netty.constant.CommandIdConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 命令处理器工厂
 */
@Component
@Slf4j
public class CommandHandlerFactory implements ApplicationContextAware {
    
    // 命令ID到处理器的映射
    private final Map<Short, CommandHandler> handlerMap = new HashMap<>();
    
    /**
     * 获取命令处理器
     * @param commandId 命令ID
     * @return 命令处理器，不存在则返回null
     */
    public CommandHandler getHandler(short commandId) {
        // 对于响应命令，获取对应的请求命令ID
        if (CommandIdConstants.isResponse(commandId)) {
            commandId = CommandIdConstants.getRequestCommandId(commandId);
        }
        return handlerMap.get(commandId);
    }
    
    /**
     * 注册命令处理器
     * @param handler 命令处理器
     */
    public void registerHandler(CommandHandler handler) {
        short commandId = handler.getCommandId();
        if (handlerMap.containsKey(commandId)) {
            log.warn("命令处理器已存在，命令ID：{}", commandId);
        } else {
            handlerMap.put(commandId, handler);
            log.info("注册命令处理器：命令ID={}, 处理器={}", commandId, handler.getClass().getSimpleName());
        }
    }
    
    /**
     * 从Spring容器中获取所有CommandHandler实例并注册
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 获取所有CommandHandler类型的Bean
        Map<String, CommandHandler> beans = applicationContext.getBeansOfType(CommandHandler.class);
        
        // 注册所有处理器
        for (CommandHandler handler : beans.values()) {
            registerHandler(handler);
        }
        
        log.info("共注册命令处理器：{}个", handlerMap.size());
    }
}