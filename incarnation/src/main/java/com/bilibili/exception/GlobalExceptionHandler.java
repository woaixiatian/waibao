package com.bilibili.exception;
import com.bilibili.define.ReturnCode;
import com.bilibili.net.http.controller.BaseController;
import com.bilibili.net.http.dto.response.Result;
import com.bilibili.util.Log;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler implements BaseController{
	// 服务器全局异常处理
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public Result error(Exception exception) throws Exception {
		Log.error("消息处理失败，出现未知错误！", exception);
		return setResult(ReturnCode.UNKNOWN_ERROR.getCode(),"消息处理失败,出现未知错误！");
	}

}