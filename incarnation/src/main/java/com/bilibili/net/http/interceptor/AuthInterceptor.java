package com.bilibili.net.http.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 获取请求的URI
		String uri = request.getRequestURI();
		// 只有登录消息可以通过拦截器
		/*if (uri.indexOf("/login/login") >= 0 || uri.indexOf("/test") >= 0) {
			return true;
		}*/

		/*Long player_id = (Long) request.getSession()
				.getAttribute(Define.PLAYER_ID);
		if (player_id != null) {
			// request.setAttribute(Define.PLAYER_ID, player_id);
			return true;
		}

		response.sendRedirect("/error/usernotlogin");*/
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}