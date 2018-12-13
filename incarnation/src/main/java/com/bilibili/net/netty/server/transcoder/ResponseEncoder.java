package com.bilibili.net.netty.server.transcoder;

import com.bilibili.define.Define;
import com.bilibili.net.netty.dto.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <pre>
 * 数据包格式
 * +——----———+——----——+——----——+——-----+
 * |  包头	|  结果码    |  长度       |   数据     |
 * +——----+——----——+——----——+——----——+
 * </pre>
 * @author xusong
 *
 */
public class ResponseEncoder extends MessageToByteEncoder<Response> {
	@Override
	protected void encode(ChannelHandlerContext ctx, Response response, ByteBuf buffer) throws Exception {
		//包头
		buffer.writeInt(Define.HEAD_FLAG);
		//结果码
		buffer.writeInt(response.getStateCode());
		//长度
		int lenth = response.getData()==null? 0 : response.getData().length;
		if(lenth <= 0){
			buffer.writeInt(lenth);
		}else{
			buffer.writeInt(lenth);
			buffer.writeBytes(response.getData());
		}
	}
}
