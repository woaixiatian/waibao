package com.bilibili.net.http.dto.response;

import com.bilibili.bean.Bean;

import java.util.HashMap;
import java.util.Map;

/**
 * describe :统一的数据交互格式
 * author : xusong
 * createTime : 2018/4/11
 */
public class Result {
    private int code;
    private String msg;
    private Map<String, Object> datas = new HashMap<String, Object>();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getDatas() {
        return datas;
    }

    public void addData(Bean data) {
        if (data != null){
            datas.put(data.getBeanName(), data);
        }
    }
    public void addData(String name, Object data)
    {
        if (data!= null){
            datas.put(name, data);
        }
    }
}
