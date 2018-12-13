package com.bilibili.net.http.controller;

import com.bilibili.define.ReturnCode;
import com.bilibili.net.http.dto.response.Result;

/**
 * describe :控制层基类
 * author : xusong
 * createTime : 2018/5/30
 */
public interface BaseController {
    default Result setResult(int code,String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    default Result setSuccess(){
        return setResult(ReturnCode.SUCCESS.getCode(),"success");
    }
}
