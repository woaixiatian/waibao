package com.bilibili.net.http.dto.request;

import lombok.Data;

import java.util.List;

/**
 * describe :
 * author : xusong
 * createTime : 2018/4/20
 */
@Data
public class DemoParam {
    private String name;
    private Integer age;
    private List<Integer> list;
}
