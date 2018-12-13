package com.bilibili.net.netty.server.invoker;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 命令执行器
 * @author xusong
 *
 */
@Getter
@Setter
public class Invoker {
	
	/**
	 * 方法
	 */
	private Method method;
	
	/**
	 * 目标对象
	 */
	private Object target;

	/**
	 * 创建一个新的Invoker对象
	 * @param method
	 * @param target
	 * @return
	 */
	public static Invoker newInstence(Method method, Object target){
		Invoker invoker = new Invoker();
		invoker.setMethod(method);
		invoker.setTarget(target);
		return invoker;
	}
	
	/**
	 * 执行
	 * @param paramValues 传入方法入参
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public Object invoke(Object... paramValues){
		try {
			return method.invoke(target, paramValues);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
