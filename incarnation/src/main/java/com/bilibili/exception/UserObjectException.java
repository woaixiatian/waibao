package com.bilibili.exception;

/**
 * Created by XuSong on 2017/9/6.
 */
public class UserObjectException extends RuntimeException {
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserObjectException(String msg) {
        this.msg = msg;
    }

    public UserObjectException() {
    }
}
