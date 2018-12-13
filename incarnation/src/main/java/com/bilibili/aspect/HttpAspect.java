package com.bilibili.aspect;

import com.bilibili.bean.user.ObjectBean;
import com.bilibili.data.manager.UserManager;
import com.bilibili.net.http.dto.response.Result;
import com.bilibili.util.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by XuSong on 2017/9/6.
 */
@Aspect
@Component
public class HttpAspect {
    @Autowired
    private UserManager userManager;

    //设置公用的切点
    @Pointcut("execution(public * com.bilibili.net.http.controller..*(..))")
    private void log() {
    }

    //记录传入参数信息
    @Before("log()")
    public void doBefor(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //url
        Log.info("url={}", request.getRequestURL());
        //method
        Log.info("method={}", request.getMethod());
        //ip
        Log.info("ip={}", request.getRemoteAddr());
        //类方法
        Log.info("class_method={}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        //参数
        Log.info("args={}", joinPoint.getArgs());
    }

    @After("log()")
    public void doAfter() {

    }

    //添加同步数据
    @AfterReturning(returning = "result", pointcut = "log()")
    public Result doAfterReturning(Result result) {
        Map<String, ArrayList<ObjectBean>> mapSyn = userManager.getMapSyn();
        if (mapSyn != null) {
            result.addData("SyncData", mapSyn);
        }
        return result;
    }
}
