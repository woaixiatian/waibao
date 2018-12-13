package com.bilibili.define;

/**
 *
 * Created by xusong on 2018/8/30.
 */
public interface ModuleAndCommandId {
    /**
     * hello模块
     */
    short HELLO = 1;
    interface HelloCommand{
        short HELLO = 1;//hello 命令
        short TEST = 2;//测试命令
    }
}
