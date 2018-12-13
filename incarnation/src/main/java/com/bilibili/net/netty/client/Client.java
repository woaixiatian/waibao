package com.bilibili.net.netty.client;

import com.bilibili.net.netty.client.transcoder.RequestEncoder;
import com.bilibili.net.netty.client.transcoder.ResponseDecoder;
import com.bilibili.net.netty.dto.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

/**
 * netty客户端入门
 * 
 * @author xusong
 * 
 */
@Component
public class Client {
	
	/**
	 * 服务类
	 */
	Bootstrap bootstrap = new Bootstrap();

	/**
	 * 会话
	 */
	private Channel channel;

	/**
	 * 线程池
	 */
	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	/**
	 * 初始化
	 */
	@PostConstruct
	public void init() {
		
		// 设置循环线程组事例
		bootstrap.group(workerGroup);

		// 设置channel工厂
		bootstrap.channel(NioSocketChannel.class);

		// 设置管道
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ResponseDecoder());
				ch.pipeline().addLast(new RequestEncoder());
			}
		});
	}

	/**
	 * 连接
	 * @throws InterruptedException
	 */
	public void connect() throws InterruptedException {

		// 连接服务端
		ChannelFuture connect = bootstrap.connect(new InetSocketAddress("127.0.0.1", 10101));
		connect.sync();
		channel = connect.channel();
	}

	/**
	 * 关闭
	 */
	public void shutdown() {
		workerGroup.shutdownGracefully();
	}

	/**
	 * 获取会话
	 * 
	 * @return
	 */
	public Channel getChannel() {
		return channel;
	}
	
	/**
	 * 发送消息
	 * @param request
	 * @throws InterruptedException 
	 */
	public void sendRequest(Request request) throws InterruptedException{
		if(channel == null || !channel.isActive()){
			connect();
		}
		channel.writeAndFlush(request);
	}

	public static void main(String[] args) throws InterruptedException {
		Client client = new Client();
		client.init();
		Request request = new Request();
		request.setCmd((short) 1);
		request.setModule((short) 1);
		request.setData("hello,xusong!".getBytes());
		client.sendRequest(request);

	}
	
}
