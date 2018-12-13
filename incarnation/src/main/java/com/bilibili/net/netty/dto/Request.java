package com.bilibili.net.netty.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息对象
 * @author xusong
 *
 */
@Setter
@Getter
public class Request {
	
	/**
	 * 模块号
	 */
	private short module;
	
	/**
	 * 命令号
	 */
	private short cmd;
	
	/**
	 * 数据
	 */
	private byte[] data;

}
