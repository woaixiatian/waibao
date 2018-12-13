package com.bilibili.net.netty.server.controller;

import com.bilibili.annotation.SocketCommand;
import com.bilibili.annotation.SocketModule;
import com.bilibili.define.ModuleAndCommandId;
import org.springframework.stereotype.Controller;

/**
 * Created by xusong on 2018/8/30.
 */
@Controller
@SocketModule(module = ModuleAndCommandId.HELLO)
public class HelloController {
    @SocketCommand(cmd = ModuleAndCommandId.HelloCommand.HELLO)
    public void hello(){

    }
    @SocketCommand(cmd = ModuleAndCommandId.HelloCommand.TEST)
    public void test(){

    }
}
