package com.bilibili.net.http.dto.response;

import com.bilibili.net.http.dto.request.DemoParam;
import lombok.Data;

import java.util.List;

/**
 * describe :
 * author : xusong
 * createTime : 2018/4/20
 */
@Data
public class DemoResponse {
    private List<Integer> list;
    private DemoParam param;
}
