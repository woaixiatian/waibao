package com.bilibili.util;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetUtil {
	// 判断是否为内网地址
	private boolean isInner(String ip) {
		String reg = "(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})";
		Pattern p = Pattern.compile(reg);
		Matcher matcher = p.matcher(ip);
		return matcher.find();
	}

	// 获取远程IP地址
	public String getRemoteAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip != null && ip.contains(",")) {
			String[] arrIpList = ip.split(",");
			for (int i = 0; i < arrIpList.length; ++i) {
				ip = arrIpList[i];
				// 如果依然是内网IP，则寻找下一级
				if (!isInner(ip)) {
					break;
				}
			}
		}

		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
