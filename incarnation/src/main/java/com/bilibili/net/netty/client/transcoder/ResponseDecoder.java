package com.bilibili.net.netty.client.transcoder;

import com.bilibili.define.Define;
import com.bilibili.net.netty.dto.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * <pre>
 * 数据包格式
 * +——----——+——-----——+——----——+——----——+——----——+——----——+
 * |  包头	|  模块号      |  命令号    |  结果码    |  长度       |   数据     |  
 * +——----——+——-----——+——----——+——----——+——----——+——----——+
 * </pre>
 * @author xusong
 *
 */
public class ResponseDecoder extends ByteToMessageDecoder {
	
	/**
	 * 数据包基本长度
	 */
	public static int BASE_LENTH = 4 + 2 + 2 + 4 + 4;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
		
		while(true){
			if(buffer.readableBytes() >= BASE_LENTH){
				//第一个可读数据包的起始位置
				int beginIndex;
				
				while(true) {
					//包头开始游标点
					beginIndex = buffer.readerIndex();
					//标记初始读游标位置
					buffer.markReaderIndex();
					if (buffer.readInt() == Define.HEAD_FLAG) {
						break;
					}
					//未读到包头标识略过一个字节
					buffer.resetReaderIndex();
					buffer.readByte();
					
					//不满足
					if(buffer.readableBytes() < BASE_LENTH){
						return ;
					}
				}
				//读取模块号命令号
				short module = buffer.readShort();
				short cmd = buffer.readShort();
				
				int stateCode = buffer.readInt();
				
				//读取数据长度 
				int lenth = buffer.readInt();
				if(lenth < 0 ){
					ctx.channel().close();
				}
				
				//数据包还没到齐
				if(buffer.readableBytes() < lenth){
					buffer.readerIndex(beginIndex);
					return ;
				}
				
				//读数据部分
				byte[] data = new byte[lenth];
				buffer.readBytes(data);
				
				Response response = new Response();
				response.setModule(module);
				response.setCmd(cmd);
				response.setStateCode(stateCode);
				response.setData(data);
				//解析出消息对象，继续往下面的handler传递
				out.add(response);
			}else{
				break;
			}
		}
		//数据不完整，等待完整的数据包
		return ;
	}

}
