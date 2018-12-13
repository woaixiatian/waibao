package com.bilibili.net.http.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * describe :
 * author : xusong
 * createTime : 2018/5/30
 */
@Getter
@Setter
public class PlayerTo {
    private Long player_id;

    private Integer level;//玩家等级

    private String name;//玩家名字
}
