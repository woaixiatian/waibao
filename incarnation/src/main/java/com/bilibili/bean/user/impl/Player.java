package com.bilibili.bean.user.impl;

import com.bilibili.annotation.Table;
import com.bilibili.bean.user.ObjectBean;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * describe :玩家表，记录玩家相关信息
 * author : xusong
 * createTime : 2018/5/30
 */
@Table(name = "usr_player")
@Getter
@Setter
public class Player extends ObjectBean {
    private Integer level;//玩家等级
    private String name;//玩家名字
    private List<Integer> address;//玩家地址 x,y,z
}
