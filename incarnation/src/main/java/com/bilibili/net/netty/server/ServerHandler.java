package com.bilibili.net.netty.server;

import com.bilibili.net.netty.dto.Request;
import com.bilibili.net.netty.dto.Response;
import com.bilibili.net.netty.server.invoker.Invoker;
import com.bilibili.net.netty.server.invoker.InvokerManager;
import com.bilibili.net.netty.server.session.Session;
import com.bilibili.util.Log;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * Created by xusong on 2018/8/22.
 */
public class ServerHandler extends SimpleChannelInboundHandler<Request> {
    @Override
    protected void channelRead0(ChannelHandlerContext context, Request request) throws Exception {

        Channel channel = context.channel();
        Session session = new Session(channel);
        dealMessage(session, request);
    }

    private void dealMessage(Session session, Request request) {
        short module = request.getModule();
        short cmd = request.getCmd();
        byte[] data = request.getData();
        String s = new String(data);
        System.out.println(s);
        Log.info("接收到客户端消息，module={},cmd={}",module,cmd);
        //获取执行器
        Invoker invoker = InvokerManager.getInvoker(module, cmd);
        Response response = new Response();
        if (invoker != null){
            //执行器不为空

        }else {
            //执行器为空
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive==========");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive==========");
        super.channelInactive(ctx);
    }


}
