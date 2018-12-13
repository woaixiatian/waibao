package com.bilibili.net.netty.client;

import com.bilibili.net.netty.dto.Response;
import com.bilibili.net.netty.server.invoker.Invoker;
import com.bilibili.net.netty.server.invoker.InvokerManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 消息接受处理类
 * @author xusong
 *
 */
public class ClientHandler extends SimpleChannelInboundHandler<Response> {
	

	/**
	 * 接收消息
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {

		handlerResponse(response);
	}
	
	
	/**
	 * 消息处理
	 */
	private void handlerResponse(Response response){
		
		//获取命令执行器
		Invoker invoker = InvokerManager.getInvoker(response.getModule(), response.getCmd());
		if(invoker != null){
			try {
				invoker.invoke(response.getStateCode(), response.getData());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			//找不到执行器
			System.out.println(String.format("module:%s  cmd:%s 找不到命令执行器", response.getModule(), response.getCmd()));
		}
	}

	/**
	 * 断开链接
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

	}
}
