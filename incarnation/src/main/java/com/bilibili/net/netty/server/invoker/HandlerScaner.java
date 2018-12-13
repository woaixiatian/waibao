package com.bilibili.net.netty.server.invoker;

import com.bilibili.annotation.SocketCommand;
import com.bilibili.annotation.SocketModule;
import lombok.extern.java.Log;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * handler扫描器，将controller中所有添加了命令的方法扫描初始化为invocer对象，存放到InvokerManager中待使用
 * @author xusong
 *
 */
@Component
@Log
public class HandlerScaner implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	//在spring管理的类完成初始化的时候调用
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		Class<?> clazz = bean.getClass();
		SocketModule socketModule = clazz.getAnnotation(SocketModule.class);
		if (socketModule != null){
			//类上带有SocketModule注解

			//找出命令方法
			Method[] methods = clazz.getMethods();
			if(methods != null && methods.length > 0){
				for(Method method : methods){
					SocketCommand socketCommand = method.getAnnotation(SocketCommand.class);
					if(socketCommand == null){
						continue;
					}
					final short module = socketModule.module();
					final short cmd = socketCommand.cmd();

					if(InvokerManager.getInvoker(module, cmd) == null){
						InvokerManager.addInvoker(module, cmd, Invoker.newInstence(method, bean));
					}else{
						log.info("重复命令:"+"module:"+module +" "+"cmd：" + cmd);
					}
				}
			}
		}
		return bean;
	}

}
