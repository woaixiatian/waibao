package com.bilibili.define;

/**
 * describe :基础常量的定义
 * author : xusong
 * createTime : 2018/4/11
 */
public interface Define {
    // 字符键-玩家id
    String PLAYER_ID = "player_id";
    // 字符键-数据是否已经被删除
    String DELETE_FLAG = "delete_flag";
    // 字符键-玩家默认的主键名
    String DEFAULT_KEY = "-1";
    // 字符键-返回码
    String RETURN_CODE = "return_code";
    // 玩家数据已被删除的标志 0为正常 1为删除
    int DELETED = 1;
    //包头信息
    int HEAD_FLAG = -10086;
}
