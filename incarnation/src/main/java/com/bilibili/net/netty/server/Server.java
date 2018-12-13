package com.bilibili.net.netty.server;

import com.bilibili.net.netty.server.transcoder.RequestDecoder;
import com.bilibili.net.netty.server.transcoder.ResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

/**
 * 描述：netty服务端启动类
 * @author xusong
 */
@Component
@Log
public class Server {

    private static final int NETTY_SERVER_PORT = 10101;

    /**
     * 启动入口方法
     */
    public void start() {

        // 服务类
        ServerBootstrap b = new ServerBootstrap();

        // 创建boss和worker
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //创建用于处理业务逻辑的线程组
        final EventLoopGroup logicGroup = new NioEventLoopGroup();

        try {
            // 设置循环线程组事例
            b.group(bossGroup, workerGroup);

            // 设置channel工厂
            b.channel(NioServerSocketChannel.class);

            // 设置管道
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new RequestDecoder());
                    ch.pipeline().addLast(new ResponseEncoder());
                    ch.pipeline().addLast(logicGroup, new ServerHandler());

                }
            });

            // 链接缓冲池队列大小
            b.option(ChannelOption.SO_BACKLOG, 2048);

            // 绑定端口
            ChannelFuture future = b.bind(NETTY_SERVER_PORT).sync();

            log.info("netty 服务端启动成功，端口号为：" + NETTY_SERVER_PORT);

            //等待服务端关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logicGroup.shutdownGracefully();
        }
    }

}
