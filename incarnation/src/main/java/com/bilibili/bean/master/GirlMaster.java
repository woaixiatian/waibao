package com.bilibili.bean.master;

import com.bilibili.annotation.Table;
import lombok.Getter;

/**
 * describe :
 * author : xusong
 * createTime : 2018/4/16
 */
@Table(name="mst_girl")
@Getter
public class GirlMaster extends MasterBean{
    private String name;
    private Integer age;
}
