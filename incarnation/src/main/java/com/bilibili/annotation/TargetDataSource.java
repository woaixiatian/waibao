package com.bilibili.annotation;

import java.lang.annotation.*;

/**
 * describe :
 * author : xusong
 * createTime : 2018/7/3
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String name();
}