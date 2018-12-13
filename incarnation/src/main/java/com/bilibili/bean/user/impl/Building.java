package com.bilibili.bean.user.impl;

import com.bilibili.annotation.Key;
import com.bilibili.annotation.Table;
import com.bilibili.bean.user.ObjectBean;
import lombok.Getter;
import lombok.Setter;

/**
 * describe:建筑表
 * auther: xusong
 * createTime: 2018/9/7 14:22
 */
@Table(name = "usr_building")
@Getter
@Setter
public class Building extends ObjectBean {
    @Key
    private Integer building_id;//建筑ID

    private Integer type;//建筑类型

    private Integer level;//建筑等级

    private Integer position;//建筑地址

    private Integer unlock_level;//解锁等级
}
