package com.bilibili.util;
import java.lang.reflect.Field;
import java.sql.Timestamp;
/**
 * describe :proto转换工具类proto_class类和普通Java类的相互转换
 * author : xusong
 * createTime : 2018/7/12
 */
public class ProtoUtil {

    public  <B,P> P beanToProto(B bean,P proto) throws IllegalAccessException, InstantiationException, NoSuchFieldException {

        Class<?> bean_class = bean.getClass();

        Class<?> protoClass = proto.getClass();
        Field[] bean_fields = bean_class.getDeclaredFields();

        for (Field bean_field : bean_fields) {
            boolean b = isSimpleType(bean_field.getType());
            if (b){
                //简单类型
                bean_field.setAccessible(true);
                String name = bean_field.getName() + "_";
                Object value = bean_field.get(bean);
                Field field = protoClass.getDeclaredField(name);
                field.setAccessible(true);
                field.set(proto,value);
            }else {
                //复杂类型,待扩展
            }
        }

        return proto;
    }

    public <B,P> B protoToBean(Class<B> bean_class,P proto) throws IllegalAccessException, InstantiationException, NoSuchFieldException {

        Class<?> proto_class = proto.getClass();
        B bean = bean_class.newInstance();

        Field[] bean_fields = bean_class.getDeclaredFields();
        for (Field bean_field : bean_fields) {
            boolean b = isSimpleType(bean_field.getType());
            if (b){
                //简单类型
                bean_field.setAccessible(true);
                String name = bean_field.getName() + "_";

                Field proto_field = proto_class.getDeclaredField(name);
                proto_field.setAccessible(true);

                Object value = proto_field.get(proto);
                bean_field.set(bean,value);

            }else {
                //复杂类型

            }
        }
        return bean;
    }

    //判断是否为简单类型
    private boolean isSimpleType(Class<?> clz) {
        // 判断是否为基础类型
        if (clz.isPrimitive()) {
            return true;
        }
        try {
            return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            // 判断是否为其他类型
            if (clz == String.class || clz == Timestamp.class) {
                return true;
            }
            return false;
        }
    }
}
