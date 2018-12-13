package com.bilibili.util;

import com.bilibili.define.ReturnCode;
import com.bilibili.net.http.dto.response.Result;

public class ResultUtil {
	private static final ThreadLocal<Integer> returnCode = new ThreadLocal<Integer>();

	public static Result getResult(ReturnCode code, String msg) {
		Result result = new Result();
		result.setCode(code.getCode());
		result.setMsg(msg);

		// 将返回码注册进Session
		returnCode.set(code.getCode());
		return result;
	}

	public static Result getResult(ReturnCode code) {
		Result result = getResult(code, null);
		return result;
	}

	public static Result getSuccess() {
		Result result = getResult(ReturnCode.SUCCESS);
		return result;
	}

	public static void setException() {
		returnCode.set(ReturnCode.UNKNOWN_ERROR.getCode());
	}

	public static Integer getReturnCode() {
		return returnCode.get();
	}

	public static void clearReturnCode() {
		returnCode.remove();
	}
}
