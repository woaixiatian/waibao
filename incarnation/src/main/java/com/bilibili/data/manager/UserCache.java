package com.bilibili.data.manager;

import com.bilibili.bean.user.ObjectBean;
import com.bilibili.data.dao.CommonDAO;
import com.bilibili.define.Define;
import com.bilibili.util.Log;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.lang.model.type.ReferenceType;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * describe :玩家数据缓存&数据交互 这里应该设计两层缓存：1 本地缓存 数据同步在集群条件下 考虑采用mq或者 zk事件通知实现 2 redis缓存
 * author : xusong
 * createTime : 2018/6/21
 */
@Component
public class UserCache {
    @Autowired
    private CommonDAO commonDAO;

    /**
     * 更新对象信息
     * @return
     * @throws Exception
     */
    @CacheEvict(value = "userObject", key = "#player_id + #table_name + #key_name + #key_value")
    public boolean update(Long player_id, ObjectBean object, String table_name, Object key_value, String key_name) throws Exception {
        Class<? extends ObjectBean> clazz = object.getClass(); // 获得class

        // 除主键外的其他字段名称与其对应的值map
        Map<String, Object> dirty_fields = new HashMap<String, Object>();
        //将对象中非空的属性添加到map中
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);

            String fieldName = field.getName();
            Object fieldValue = getField(object, field);
            //TODO xu 待定 后期是否需要加入其它筛选逻辑
            if (fieldValue == null) {
                //字段为空 跳过本次循环
                continue;
            }
            dirty_fields.put(fieldName, fieldValue);
        }
        dirty_fields.put(Define.PLAYER_ID, player_id);
        dirty_fields.put(Define.DELETE_FLAG, 0);
        // 存储
        commonDAO.update(table_name, player_id, key_name, key_value, dirty_fields);
        return true;
    }

    /**
     * 删除对象信息
     *
     * @return
     * @throws Exception
     */
    @CacheEvict(value = "userObject", key = "#player_id + #table_name + #key_name + #key_value")
    public boolean delete(Long player_id, String table_name, Object key_value, String key_name) throws Exception {
        // 删除
        commonDAO.deleteLogical(table_name, player_id, key_name, key_value);
        return true;
    }

    /**
     * 新建对象信息
     *
     * @return
     * @throws Exception
     */
    //@Cacheable(value = "userObject", key = "#player_id + #table_name + #key_name +#key_value")
    public boolean insert(Long player_id, ObjectBean object,String table_name,String key_name,Object key_value) throws Exception {
        Class<? extends ObjectBean> clazz = object.getClass(); // 获得class

        Map<String, Object> fieldNameAndValueMap = new HashMap<String, Object>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);

            String fieldName = field.getName();
            Object fieldValue = getField(object, field);
            //字段非空校验，待定需不需要加
            if (fieldValue == null) {
                Log.debug("玩家【{}】插入【{}】对象失败，字段【{}】不允许为null", player_id, clazz.getSimpleName(),
                        fieldName);
                return false;
            }

            fieldNameAndValueMap.put(fieldName, fieldValue);
        }

        fieldNameAndValueMap.put(Define.PLAYER_ID, player_id);
        fieldNameAndValueMap.put(Define.DELETE_FLAG, 0);

        //验证对象是否存在
        ObjectBean obj = loadDB(clazz, table_name, player_id, key_name, key_value, false, true);
        if (obj != null){
            Log.info("玩家【{}】插入【{}】失败，key =【{}】，对象已存在",player_id,clazz.getSimpleName(),key_value);
            return false;
        }
        // 先验证是否存在被删除的对象
        ObjectBean objectDeleted = loadDB(clazz, table_name, player_id, key_name, key_value, true, true);
        // 不存在，则新插入
        if (objectDeleted == null) {
            commonDAO.insert(fieldNameAndValueMap, table_name);
        }
        // 存在已经被删除的老数据，则进行逻辑插入
        else {
            commonDAO.insertLogical(table_name, player_id, key_name, key_value, fieldNameAndValueMap);
        }

        return true;
    }

    /**
     * 读取数据库获取实体类(参数顺序与实体类中主键顺序保持一致)
     *
     * @param cls
     * @return T
     * @throws Exception
     */
    @Cacheable(value = "userObject", key = "#player_id + #table_name + #key_name +#key_value")
    public <T extends ObjectBean> T loadDB(Class<T> cls, String table_name, Long player_id, String key_name, Object key_value,
                                           Boolean include_deleted, Boolean for_update) throws Exception {
        Map<?, ?> map = commonDAO.select(table_name, player_id, key_name, key_value, include_deleted, for_update);
        if (map == null) {
            return null;
        }
        T object = mapToObject(cls, map);
        if (object == null) {
            return null;
        }
        return object;
    }

    /**
     * 读取数据库获取实体类的集合(参数顺序与实体类中主键顺序保持一致)
     *
     * @param cls
     * @return List<T>
     * @throws Exception
     */
    //@Cacheable(value = "userObjectList", key = "#player_id +#table_name")
    public <T extends ObjectBean> List<T> loadDBList(Class<T> cls, String table_name, Long player_id)
            throws Exception {
        List<Map<?, ?>> lstMaps = commonDAO.selectList(table_name, player_id);

        List<T> lstObjects = new ArrayList<T>();
        for (Map<?, ?> map : lstMaps) {
            T obj = mapToObject(cls, map);
            lstObjects.add(obj);
        }
        return lstObjects;
    }


    /**
     * map转对象
     *
     * @param cls
     * @param map
     * @return T
     * @throws Exception
     */
    private <T> T mapToObject(Class<T> cls, Map<?, ?> map) throws Exception {
        T obj = null;
        try {
            obj = (T) cls.newInstance();
        } catch (InstantiationException e) {
            Log.error("类【{}】必须要包含一个public的无参构造方法", cls.getSimpleName(), e);
        }

        // 获取实体类的Field
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);

            Object value = map.get(field.getName());
            setField(obj, field, value);
        }

        //设置玩家ID

        Field playIdFild = obj.getClass().getSuperclass().getDeclaredField(Define.PLAYER_ID);
        playIdFild.setAccessible(true);

        Long value = (Long) map.get(playIdFild.getName());
        playIdFild.set(obj, value);
        return obj;
    }

    // 将Field的值赋值：数据库或缓存中读出的值=>对象中
    private Object setField(Object object, Field field, Object value) throws
            IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {

        Type type = field.getGenericType();
        // 【简单类型】
        if (isSimpleType(field.getType())) {
            // 不需要做任何事
        }
        // 【引用类型】【泛型】
        else if (type instanceof ReferenceType || type instanceof ParameterizedType) {
            if (value == null) {
                try {
                    // 如果为空，尝试调用默认的构造方法
                    value = field.getType().newInstance();
                } catch (InstantiationException e) {
                    value = null;
                }
            } else if (value.getClass() == String.class) {
                //引用类型 用jkson转为对象
                ObjectMapper mapper = new ObjectMapper();
                JavaType javaType = mapper.getTypeFactory().constructType(type);
                value = mapper.readValue(value.toString(), javaType);
            }
        }

        field.set(object, value); // 赋值
        return value;
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

    // 获取Field的值：对象中的值=>反序列化成数据库或缓存的值
    private Object getField(Object object, Field field)
            throws IOException, IllegalArgumentException, IllegalAccessException {
        Object value = null;
        Type type = field.getGenericType();

        // 【简单类型】
        if (isSimpleType(field.getType())) {
            value = field.get(object);
        }
        // 【引用类型】【泛型】
        else if (type instanceof ReferenceType || type instanceof ParameterizedType) {
            ObjectMapper mapper = new ObjectMapper();
            value = field.get(object);
            value = mapper.writeValueAsString(value);
        }

        return value;
    }
}
