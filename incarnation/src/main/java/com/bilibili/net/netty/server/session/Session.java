package com.bilibili.net.netty.server.session;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * 会话封装类
 * @author xusong
 *
 */
public class Session{
	
	/**
	 * 绑定对象key
	 */
	public static AttributeKey<Object> ATTACHMENT_KEY  = AttributeKey.valueOf("ATTACHMENT_KEY");
	
	/**
	 * 实际会话对象
	 */
	private Channel channel;
	
	
	public Session(Channel channel) {
		this.channel = channel;
	}

	/**
	 * 会话绑定对象
	 */
	public Object getAttachment() {
		return channel.attr(ATTACHMENT_KEY).get();
	}

	/**
	 * 绑定对象
	 */
	public void setAttachment(Object attachment) {
		channel.attr(ATTACHMENT_KEY).set(attachment);
	}

	/**
	 * 移除绑定对象
	 */
	public void removeAttachment() {
		channel.attr(ATTACHMENT_KEY).remove();
	}

	/**
	 * 向会话中写入消息
	 */
	public void write(Object message) {
		channel.writeAndFlush(message);
	}

	/**
	 * 判断会话是否在连接中
	 */
	public boolean isConnected() {
		return channel.isActive();
	}

	/**
	 * 关闭
	 */
	public void close() {
		channel.close();
	}



}
