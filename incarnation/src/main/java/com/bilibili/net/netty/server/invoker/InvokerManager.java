package com.bilibili.net.netty.server.invoker;

import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 命令执行器管理者
 * @author xusong
 *
 */
@Log
public class InvokerManager {
	
    /**命令调用器，此处不做写操作，因此没有并发风险*/
    private static Map<Short, Map<Short, Invoker>> invokers = new HashMap<>();
    
    /**
     * 添加命令调用
     * @param module
     * @param cmd
     * @param invoker
     */
    public static void addInvoker(short module, short cmd, Invoker invoker){
    	Map<Short, Invoker> map = invokers.get(module);
    	if(map == null){
    		map = new HashMap<>();
    		invokers.put(module, map);
    	}
    	map.put(cmd, invoker);
    }
    
    
    /**
     * 获取命令调用
     * @param module
     * @param cmd
     */
    public static Invoker getInvoker(short module, short cmd){
    	Map<Short, Invoker> map = invokers.get(module);
    	if(map != null){
    		return map.get(cmd);
    	}
    	return null;
    }

}
