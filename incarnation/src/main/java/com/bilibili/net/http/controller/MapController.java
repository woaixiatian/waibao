package com.bilibili.net.http.controller;

import com.bilibili.bean.user.impl.Player;
import com.bilibili.data.manager.UserManager;
import com.bilibili.define.ReturnCode;
import com.bilibili.net.http.dto.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * describe :地图相关
 * author : xusong
 * createTime : 2018/5/30
 */
@RequestMapping(value = "/map")
@RestController
public class MapController implements BaseController {
    @Autowired
    private UserManager userManager;
    @Autowired
    private HttpServletRequest request;

    /**
     * 玩家位置移动
     *
     * @return
     */
    @PostMapping(value = "/address")
    public Result login(@RequestBody ArrayList<Integer> address) throws Exception {
        //request.getSession().setAttribute(Define.PLAYER_ID, 1007L);
        Player player = userManager.loadObject(Player.class);
        player.setAddress(address);
        boolean b = userManager.updateObject(player);
        if (!b){
            return setResult(ReturnCode.UPDATE_ADDRESS_ERROR.getCode(),"更新玩家位置失败！");
        }
        Result result = setSuccess();
        return result;
    }
}
