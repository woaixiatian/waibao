package com.bilibili.define;

public enum EventType {
	// 玩家相关
	EVENT_PLAYER_LOGIN(10001), 						// 玩家登陆
	
	// 消息相关
	EVENT_MSG_FINISH(40001), 							// 消息完成

	EVENT_NONE(0); 												// 无效事件ID

	int nType;

	EventType(int type) {
		nType = type;
	}

	public int getType() {
		return nType;
	}
}
