package com.bilibili.event;

import com.bilibili.define.EventType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class EventDispatcher implements InitializingBean {

	private static Map<EventType, List<Consumer<Map<String, Object>>>> eventMap = null;

	public void regist(EventType type, Consumer<Map<String, Object>> callback) {
		if (eventMap == null) {
			synchronized (this) {
				eventMap = new HashMap<>();
			}
		}

		// 查看事件列表是否为空
		List<Consumer<Map<String, Object>>> callbacks = eventMap.get(type);
		if (callbacks == null) {
			callbacks = new ArrayList<Consumer<Map<String, Object>>>();
			eventMap.put(type, callbacks);
		}

		callbacks.add(callback);
	}

	public void dispatch(EventType eventType, Map<String, Object> params) {
		List<Consumer<Map<String, Object>>> callbacks = eventMap.get(eventType);
		if (callbacks == null) {
			return;
		}

		for (Consumer<Map<String, Object>> callback : callbacks) {
			callback.accept(params);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

}