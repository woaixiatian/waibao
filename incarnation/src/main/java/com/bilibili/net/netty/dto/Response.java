package com.bilibili.net.netty.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 回复消息
 * @author xusong
 *
 */
@Getter
@Setter
public class Response {

	/**
	 * 模块号
	 */
	private short module;

	/**
	 * 命令号
	 */
	private short cmd;
	/**
	 * 结果码
	 */
	private int stateCode;
	
	/**
	 * 数据
	 */
	private byte[] data;

	public Response(int stateCode, byte[] data) {
		this.stateCode = stateCode;
		this.data = data;
	}
	public Response(Request message) {
		this.module = message.getModule();
		this.cmd = message.getCmd();
	}

	public Response(short module, short cmd, byte[] data){
		this.module = module;
		this.cmd = cmd;
		this.data = data;
	}


	public Response() {
	}
}
