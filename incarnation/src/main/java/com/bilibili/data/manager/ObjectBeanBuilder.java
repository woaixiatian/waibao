package com.bilibili.data.manager;

import com.bilibili.bean.user.ObjectBean;
import com.bilibili.define.Define;
import com.bilibili.util.Log;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Service
public class ObjectBeanBuilder {
	public <T extends ObjectBean> T build(Class<T> cls, Object key) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException {
		T object = null;

		// 如果是以PlayerID作为主键，则调用默认的构造方法
		if (key.equals(Define.DEFAULT_KEY)) {
			// 执行默认的构造函数
			object = cls.newInstance();
		} else {
			// 如果有联合主键的构造函数，则执行该函数
			Constructor<T> constructor = null;
			try {
				constructor = cls.getDeclaredConstructor(Object.class);
			} catch (NoSuchMethodException e) {
				Log.error("新建[{}]错误，类[{}]必须要包含一个参数为Object的构造方法", cls.getSimpleName(), cls.getSimpleName(), e);
				return null;
			}
			object = constructor.newInstance(key);
			object.setKeyValue(key);
		}

		return object;
	}
}
