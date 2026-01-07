package cn.fuguang.communicate.command;

import java.util.Map;

/**
 * 发送指令的顶层接口
 */
public interface Command {

    void push(Map<String, Object> params);
}
