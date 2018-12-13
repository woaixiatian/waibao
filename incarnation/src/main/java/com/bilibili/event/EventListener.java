package com.bilibili.event;

import java.util.Map;

public interface EventListener {
	void handleEvent(int nItemID, int nAddNum, Map<String, Object> params);
}