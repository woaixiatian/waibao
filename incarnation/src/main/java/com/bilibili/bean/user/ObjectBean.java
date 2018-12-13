package com.bilibili.bean.user;

import com.bilibili.annotation.Key;
import com.bilibili.annotation.Table;
import com.bilibili.bean.Bean;
import com.bilibili.bean.ObjectInfo;
import com.bilibili.define.Define;
import com.bilibili.util.Log;
import com.bilibili.util.PackageScanner;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author xusong
 * @date 2017年9月28日 下午4:44:05
 * @details 实体类基类, 代理子类, 获取子类代理对象(玩家表实体类继承此类)
 */
public class ObjectBean implements Bean {
    private static Map<Class<?>, ObjectInfo> mapObjectMapper = new HashMap<Class<?>, ObjectInfo>();
    @JsonIgnore
    private Long player_id; // 玩家id
    @JsonIgnore
    private int delete_flag; // 是否删除的标记 0:未删除  1：已删除

    public Long getPlayer_id() {
        return player_id;
    }

    public final int getDelete_flag() {
        return delete_flag;
    }

    public final void setDelete_flag(int delete_flag) {
        this.delete_flag = delete_flag;
    }
    @JsonIgnore
    public final Object getKeyValue() {
        ObjectInfo info = mapObjectMapper.get(this.getClass());
        if (info.getKeyName().equals(Define.DEFAULT_KEY)) {
            return Define.DEFAULT_KEY;
        }
        Field key = null;
        try {
            key = this.getClass().getDeclaredField(info.getKeyName());
            key.setAccessible(true);
            return key.get(this);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            Log.error("获取主键值失败{}", e);
        }
        return key;
    }

    public final boolean setKeyValue(Object value) {
        ObjectInfo info = mapObjectMapper.get(this.getClass());
        if (info.getKeyName().equals(Define.DEFAULT_KEY)) {
            return false;
        }
        Field key = null;
        try {
            key = this.getClass().getDeclaredField(info.getKeyName());
            key.setAccessible(true);
            key.set(this, value);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            Log.error("设置主键值失败{}", e);
        }
        return true;
    }

    public static final void initObjectInfo() {
        Set<Class<?>> clazzs = PackageScanner.findFileClass("com.bilibili.bean.user.impl");
        for (Class<?> clazz : clazzs) {
            // 只有继承自ObjectBean的类型才会被扫描
            if (!ObjectBean.class.isAssignableFrom(clazz)) {
                continue;
            }

            Table table = AnnotationUtils.findAnnotation(clazz, Table.class);
            // 找到Table注解
            if (table != null) {
                ObjectInfo info = new ObjectInfo();
                String tableName = table.name();
                info.setKeyName(Define.DEFAULT_KEY);
                info.setTableName(tableName);

                // 查找Keys注解，组成查询条件
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Key.class)) {
                        //一级主键赋值
                        info.setKeyName(field.getName());
                    }
                }

                mapObjectMapper.put(clazz, info);
                Log.debug("映射到[{}]类所对应的数据库表[{}]，查询条件为[{}]", clazz.getSimpleName(), info.getTableName(),
                        info.getKeyName());
            }
        }
    }

    public static final ObjectInfo getObjectInfo(Class<?> cls) {
        ObjectInfo info = mapObjectMapper.get(cls);
        return info;
    }

    public void setPlayer_id(Long player_id) {
        this.player_id = player_id;
    }
}