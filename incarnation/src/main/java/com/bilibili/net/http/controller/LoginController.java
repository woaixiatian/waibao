package com.bilibili.net.http.controller;

import com.bilibili.bean.user.impl.Building;
import com.bilibili.bean.user.impl.Player;
import com.bilibili.data.manager.UserManager;
import com.bilibili.define.Define;
import com.bilibili.define.ReturnCode;
import com.bilibili.net.http.dto.request.PlayerTo;
import com.bilibili.net.http.dto.response.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * describe :登录
 * author : xusong
 * createTime : 2018/5/30
 */
@RequestMapping(value = "/login")
@RestController
public class LoginController implements BaseController {
    @Autowired
    private UserManager userManager;
    @Autowired
    private HttpServletRequest request;
    /**
     * 用户登录
     *
     * @param player_id 玩家ID
     * @return
     */
    @GetMapping(value = "/login/{player_id}")
    public Result login(@PathVariable Long player_id) throws Exception {


        Player player = userManager.loadObject(Player.class);
        if (player == null) {
            //登录失败
            return setResult(ReturnCode.LOGIN_ERROR.getCode(), "登录失败！");
        }else {
            //登陆成功
            //将ID放到session中
            request.getSession().setAttribute(Define.PLAYER_ID, player_id);
        }
        return setSuccess();
    }

    /**
     * 用户注册
     *
     * @param playerTo 玩家ID
     * @return
     */
    @PostMapping(value = "/regist")
    public Result regist(@RequestBody PlayerTo playerTo) throws Exception {
        //将ID放到session中
        request.getSession().setAttribute(Define.PLAYER_ID, playerTo.getPlayer_id());
        //注册新用户
        Player player = new Player();
        player.setLevel(playerTo.getLevel());
        player.setName(playerTo.getName());
        userManager.insertObject(player);
        return setSuccess();
    }

    /**
     * 测试
     *
     * @return
     */
    @GetMapping(value = "/load")
    public Result load() throws Exception {
        //将ID放到session中
        request.getSession().setAttribute(Define.PLAYER_ID, 1007L);
        Player player = userManager.loadObject(Player.class);
        Result result = setSuccess();
        result.addData(player);
        return result;
    }

    @GetMapping(value = "/update")
    public Result update() throws Exception {
        //将ID放到session中
        request.getSession().setAttribute(Define.PLAYER_ID, 1002L);
        Player player = new Player();
        player.setLevel(777);
        userManager.updateObject(player);
        Result result = setSuccess();
        result.addData(player);
        return result;
    }

    @GetMapping(value = "/del")
    public Result del() throws Exception {
        userManager.deleteObject(Player.class);
        Result result = setSuccess();
        return result;
    }

    @GetMapping(value = "/insert")
    public Result insert() throws Exception {
        request.getSession().setAttribute(Define.PLAYER_ID, 153001000010005L);

        Building building = new Building();
        building.setBuilding_id(1);
        building.setLevel(1);
        building.setPosition(1);
        building.setType(1001);
        building.setUnlock_level(9);
        userManager.insertObject(building);

        Result result = setSuccess();
        return result;
    }
}
