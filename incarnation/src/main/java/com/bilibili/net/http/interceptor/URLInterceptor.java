package com.bilibili.net.http.interceptor;

import com.bilibili.define.Define;
import com.bilibili.define.EventType;
import com.bilibili.define.ReturnCode;
import com.bilibili.event.EventDispatcher;
import com.bilibili.util.Log;
import com.bilibili.util.NetUtil;
import com.bilibili.util.ResultUtil;
import com.bilibili.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class URLInterceptor implements HandlerInterceptor {
	@Autowired
	private EventDispatcher eventDispatcher;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		NetUtil netUtil = (NetUtil) SpringUtil.getBean("NetUtil");
		// 获取请求的IP
		String IP = netUtil.getRemoteAddress(request);
		// 获取请求的URI
		String uri = request.getRequestURI();
		// 获取用户ID
		Long player_id = (Long) request.getSession().getAttribute(Define.PLAYER_ID);

		// TODO 查看是否为黑名单
		// if (isInBlackList(IP)) {
		// response.sendRedirect("/error/usernotlogin");
		// return false;
		// }

		Log.debug("----------[{}]消息开始：来自IP[{}]的玩家[{}]，SESSION为[{}]----------", uri, IP, player_id,
				request.getSession().getId());
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		NetUtil netUtil = (NetUtil) SpringUtil.getBean("NetUtil");
		// 获取请求的IP
		String IP = netUtil.getRemoteAddress(request);
		// 获取请求的URI
		String uri = request.getRequestURI();
		// 获取用户ID
		Long player_id = (Long) request.getSession().getAttribute(Define.PLAYER_ID);

		Integer return_code = ResultUtil.getReturnCode();
		ResultUtil.clearReturnCode();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("URI", uri);
		params.put(Define.RETURN_CODE, return_code);
		eventDispatcher.dispatch(EventType.EVENT_MSG_FINISH, params);

		if (return_code == ReturnCode.SUCCESS.getCode()) {
			Log.debug("----------[{}]消息结束：来自IP[{}]的玩家[{}]，SESSION为[{}]，返回码为[{}], 玩家信息提交成功----------", uri, IP,
					player_id, request.getSession().getId(), return_code);
		} else {
			Log.debug("----------[{}]消息结束：来自IP[{}]的玩家[{}]，SESSION为[{}]， 返回码为[{}], 没有提交玩家信息----------", uri, IP,
					player_id, request.getSession().getId(), return_code);
		}
	}
}