package com.bilibili.net.netty.server.session;

import com.bilibili.net.netty.dto.Response;
import com.google.protobuf.GeneratedMessage;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理者
 * @author xusong
 *
 */
public class SessionManager {

	/**
	 * 在线会话
	 */
	private static  ConcurrentHashMap<Long, Session> onlineSessions = new ConcurrentHashMap<>();
	
	/**
	 * 加入
	 * @param playerId
	 * @return
	 */
	public static boolean putSession(long playerId, Session session){
		boolean playerNotExist = !onlineSessions.containsKey(playerId);
		if(playerNotExist){
			onlineSessions.put(playerId, session);
			return true;
		}
		return false;
	}
	
	/**
	 * 移除
	 * @param playerId
	 */
	public static Session removeSession(long playerId){
		return onlineSessions.remove(playerId);
	}
	
	/**
	 * 发送消息,[直接发送byte[]]
	 */
	public static  void sendMessage(long playerId,int stateCode, byte[] message){
		Session session = onlineSessions.get(playerId);
		if (session != null && session.isConnected()) {
			Response response = new Response(stateCode, message);
			session.write(response);
		}
	}
	
	/**
	 * 发送消息[protoBuf协议]
	 * @param <T>
	 * @param playerId
	 * @param message
	 */
	public static <T extends GeneratedMessage> void sendMessage(long playerId, int stateCode, T message){
		Session session = onlineSessions.get(playerId);
		if (session != null && session.isConnected()) {
			Response response = new Response(stateCode, message.toByteArray());
			session.write(response);
		}
	}
	
	/**
	 * 是否在线
	 * @param playerId
	 * @return
	 */
	public static boolean isOnlinePlayer(long playerId){
		return onlineSessions.containsKey(playerId);
	}
	
	/**
	 * 获取所有在线玩家
	 * @return
	 */
	public static Set<Long> getOnlinePlayers() {
		return Collections.unmodifiableSet(onlineSessions.keySet());
	}
}
