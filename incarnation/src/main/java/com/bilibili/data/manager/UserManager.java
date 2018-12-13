package com.bilibili.data.manager;

import com.bilibili.bean.ObjectInfo;
import com.bilibili.bean.user.ObjectBean;
import com.bilibili.define.Define;
import com.bilibili.event.EventDispatcher;
import com.bilibili.exception.UserObjectException;
import com.bilibili.util.Log;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * describe :玩家数据管理器
 * author : xusong
 * createTime : 2018/6/14
 */
@Component
public class UserManager {
    //设置过期时间
    private int EXPIRE_TIME = 60 * 60;
    @Autowired
    private UserCache userCache;
    //用于为前端同步更改数据
    ConcurrentHashMap<Long, Map<String, ArrayList<ObjectBean>>> mapForSyn = new ConcurrentHashMap<Long, Map<String, ArrayList<ObjectBean>>>();
    //private Map<Long, Map<Class<?>, Map<Object, ObjectBean>>> objectBeanMap = new MyLinkedHashMap<Long, Map<Class<?>, Map<Object, ObjectBean>>>();

    //设计java内存缓存 配合IP绑定得方式来实现 缓存数据同步,缓存以玩家为单位进行隔离
    Cache<Long, Map<Class<?>, Map<Object, ObjectBean>>> objectBeanMap = CacheBuilder.newBuilder()
            //设置并发级别为16，并发级别是指可以同时写缓存的线程数
            .concurrencyLevel(16)
            //设置缓存最大容量为10000，超过10000之后就会按照LRU最近虽少使用算法来移除缓存项
            .maximumSize(10000)
            //过期时间
            .expireAfterWrite(EXPIRE_TIME, TimeUnit.SECONDS)
            .build();

    //【ObjectBean初始化】
    UserManager(EventDispatcher eventDispatcher) {
        ObjectBean.initObjectInfo();
    }

    //////////////////////////////////////【以下是暴露出来对外访问的方法】////////////////////////////////////////

    /**
     * 通过主键查询单个用户数据
     *
     * @param cls
     * @param key 主键
     * @param <T>
     * @return
     */
    public <T extends ObjectBean> T loadObject(Class<T> cls, Object key) throws Exception {
        Long player_id = getPlayerID();
        ObjectBean object = null; // 实体类
        object = loadMem(cls, player_id, key);
        if (object != null){
            //从内存中获取到对应数据
            return (T)object;
        }

        ObjectInfo info = ObjectBean.getObjectInfo(cls);
        if (info == null) {
            Log.error("不存在类【{}】的信息", cls.getName());
            throw new UserObjectException("不存在类的信息");
        }
        String table_name = info.getTableName();
        String key_name = info.getKeyName();

        object = userCache.loadDB(cls, table_name, player_id, key_name, key, false, false);
        //添加到缓存
        if (object!= null){
            saveMem(object,getPlayerID());
        }

        return (T) object;
    }

    /**
     * 无主键查询单个玩家数据
     *
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T extends ObjectBean> T loadObject(Class<T> cls) throws Exception {
        return loadObject(cls, Define.DEFAULT_KEY);
    }

    /**
     * 无主键查询多个玩家数据
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T extends ObjectBean> List<T> loadObjectList(Class<T> cls) throws Exception {
        Long player_id = getPlayerID();
        List<ObjectBean> objectBeanList = loadMemList(cls, player_id);
        if (objectBeanList != null && objectBeanList.size() >0){
            //在缓存中拉去到对应的数据
            return (List<T>) objectBeanList;
        }
        ObjectInfo info = ObjectBean.getObjectInfo(cls);
        if (info == null) {
            Log.error("不存在类【{}】的信息", cls.getName());
            throw new UserObjectException("不存在类的信息");
        }
        String table_name = info.getTableName();

        List<T> list = userCache.loadDBList(cls, table_name, player_id);

        //添加到缓存
        if (list!= null && list.size()>0){
            saveMemList(cls,list,getPlayerID());
        }

        return list;
    }

    /**
     * 主键删除单个用户数据
     *
     * @param cls
     * @param key
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T extends ObjectBean> boolean deleteObject(Class<T> cls, Object key) throws Exception {
        ObjectInfo info = ObjectBean.getObjectInfo(cls);
        if (info == null) {
            Log.error("不存在类【{}】的信息", cls.getName());
            return false;
        }
        String key_name = info.getKeyName();

        // 如果无主键的则把key设为DEFAULT_KEY
        if (Define.DEFAULT_KEY.equals(key_name)) {
            key = Define.DEFAULT_KEY;
        }
        // 如果有主键，并且主键值为Define.DEFAULT_KEY，则返回null
        else if (Define.DEFAULT_KEY.equals(key)) {
            Log.debug("删除【{}】对象失败，主键不允许为-1", cls.getSimpleName());
            return false;
        }

        T object = null;
        // 检查是否在已存在
        object = loadObject(cls, key);
        if (object == null) {
            Log.debug("删除【{}】对象失败，主键为【{}】的对象不存在", cls.getSimpleName(), key);
            return false;
        }
        String table_name = info.getTableName();
        Object key_value = object.getKeyValue();

        //清除对应缓存
        delMem(cls,getPlayerID(),key_value);

        return userCache.delete(getPlayerID(), table_name, key_value, key_name);
    }

    /**
     * 无主键删除单个用户数据
     *
     * @param cls
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T extends ObjectBean> boolean deleteObject(Class<T> cls) throws Exception {
        return deleteObject(cls, Define.DEFAULT_KEY);
    }

    /**
     * 插入玩家数据
     *
     * @param object
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T extends ObjectBean> boolean insertObject(ObjectBean object) throws Exception {
        if (object == null){
            return false;
        }
        Class<? extends ObjectBean> clazz = object.getClass(); // 获得class
        ObjectInfo info = ObjectBean.getObjectInfo(clazz);
        if (info == null) {
            Log.error("玩家【{}】插入【{}】对象失败，不存在类【{}】的信息", getPlayerID(), clazz.getSimpleName(),
                    clazz.getSimpleName());
            return false;
        }

        String table_name = info.getTableName();
        String key_name = info.getKeyName();
        Object key_value = object.getKeyValue();
        boolean b = userCache.insert(getPlayerID(), object, table_name, key_name, key_value);
        addMapSyn(object);
        //添加到缓存
        saveMem(object,getPlayerID());
        return b;
    }

    /**
     * 跟新玩家数据
     *
     * @param object
     * @return
     * @throws Exception
     */
    public boolean updateObject(ObjectBean object) throws Exception {
        Class<? extends ObjectBean> clazz = object.getClass(); // 获得class

        ObjectInfo info = ObjectBean.getObjectInfo(clazz);
        if (info == null) {
            Log.error("玩家【{}】插入【{}】对象失败，不存在类【{}】的信息", getPlayerID(), clazz.getSimpleName(),
                    clazz.getSimpleName());
            return false;
        }
        String table_name = info.getTableName();
        String key_name = info.getKeyName();
        Object key_value = object.getKeyValue();

        boolean b = userCache.update(getPlayerID(), object, table_name, key_value, key_name);
        Object keyValue = object.getKeyValue();
        ObjectBean forSyn = null;
        if (Define.DEFAULT_KEY.equals(keyValue)) {
            forSyn = loadObject(object.getClass());
        } else {
            forSyn = loadObject(object.getClass(), object.getKeyValue());
        }
        addMapSyn(forSyn);

        //清空缓存
        delMem(object.getClass(),getPlayerID(),object.getKeyValue());
        return b;
    }

    //////////////////////////////////////【以上是暴露出来对外访问的方法】/////////////////////////////////////////

    //////////////////////////////////////【以下是封装得本地缓存相关的方法】/////////////////////////////////////////
    private ObjectBean loadMem(Class<?> cls, Long player_id, Object key_value) {
        @Nullable Map<Class<?>, Map<Object, ObjectBean>> objectClassMap = objectBeanMap.getIfPresent(player_id);
        //Map<Class<?>, Map<Object, ObjectBean>> objectClassMap = objectBeanMap.get(player_id);
        // 没找到该玩家的缓存信息
        if (objectClassMap == null) {
            return null;
        }
        Map<Object, ObjectBean> objectMap = objectClassMap.get(cls);
        // 缓存信息中没有该类型的信息
        if (objectMap == null) {
            return null;
        }
        ObjectBean objectBean = objectMap.get(key_value);
        return objectBean;
    }

    private List<ObjectBean> loadMemList(Class<?> cls, Long player_id) {
        @Nullable Map<Class<?>, Map<Object, ObjectBean>> objectClassMap = objectBeanMap.getIfPresent(player_id);
        //Map<Class<?>, Map<Object, ObjectBean>> objectClassMap = objectBeanMap.get(player_id);
        // 没找到该玩家的缓存信息
        if (objectClassMap == null) {
            return null;
        }

        Map<Object, ObjectBean> objectMap = objectClassMap.get(cls);
        // 缓存信息中没有该类型的信息
        if (objectMap == null) {
            return null;
        }

        Collection<ObjectBean> objectCollection = objectMap.values();
        List<ObjectBean> objectList = new ArrayList<ObjectBean>(objectCollection);

        return objectList;
    }

    private  boolean saveMem(ObjectBean objectBean, Long player_id) {
        @Nullable Map<Class<?>, Map<Object, ObjectBean>> objectClassMap = objectBeanMap.getIfPresent(player_id);
        //Map<Class<?>, Map<Object, ObjectBean>> objectClassMap = objectBeanMap.get(player_id);
        // 没找到该玩家的缓存信息
        if (objectClassMap == null) {
            objectClassMap = new HashMap<Class<?>, Map<Object, ObjectBean>>();
            objectBeanMap.put(player_id, objectClassMap);
        }
        // 获取ObjectBean类型
        Class<?> cls = objectBean.getClass();

        Map<Object, ObjectBean> objectMap = objectClassMap.get(cls);
        // 缓存信息中没有该类型的信息
        if (objectMap == null) {
            objectMap = new HashMap<Object, ObjectBean>();
            objectClassMap.put(cls, objectMap);
        }
        // 更新缓存信息
        objectMap.put(objectBean.getKeyValue(), objectBean);
        return true;
    }

    private <T extends ObjectBean> boolean saveMemList(Class<?> cls, List<T> objectList, Long player_id) {
        // 判断List是否为空或者长度是否为0
        if (objectList == null) {
            return false;
        }
        @Nullable Map<Class<?>, Map<Object, ObjectBean>> objectClassMap = objectBeanMap.getIfPresent(player_id);
        //Map<Class<?>, Map<Object, ObjectBean>> objectClassMap = objectBeanMap.get(player_id);
        // 没找到该玩家的缓存信息
        if (objectClassMap == null) {
            objectClassMap = new HashMap<Class<?>, Map<Object, ObjectBean>>();
            //CacheUtil.put(player_id + "" ,objectClassMap);
            objectBeanMap.put(player_id, objectClassMap);
        }

        Map<Object, ObjectBean> objectMap = objectClassMap.get(cls);
        // 缓存信息中没有该类型的信息
        if (objectMap == null) {
            objectMap = new HashMap<Object, ObjectBean>();
            objectClassMap.put(cls, objectMap);
        }
        for (T obj : objectList) {
            //覆盖全部缓存
            objectMap.put(obj.getKeyValue(), obj);
        }
        return true;
    }

    //删除单个数据
    private boolean delMem(Class<?> cls, Long player_id, Object key_value) {
        @Nullable Map<Class<?>, Map<Object, ObjectBean>> objectClassMap = objectBeanMap.getIfPresent(player_id);
        //Map<Class<?>, Map<Object, ObjectBean>> classMapMap = objectBeanMap.get(player_id);
        if (objectClassMap == null){
            return false;
        }
        Map<Object, ObjectBean> objectObjectBeanMap = objectClassMap.get(cls);

        if (objectObjectBeanMap == null){
            return false;
        }
        objectObjectBeanMap.remove(key_value);
        return true;
    }
    //////////////////////////////////////【以上是封装得本地缓存相关的方法】/////////////////////////////////////////

    /////////////////////////////////////【以下向前端同步修改数据】///////////////////////////////////////////////
    //添加同步数据
    private void addMapSyn(ObjectBean objectBean) {
        Map<String, ArrayList<ObjectBean>> synMap = mapForSyn.get(getPlayerID());
        if (synMap == null) {
            //用户同步数据初始化
            HashMap<String, ArrayList<ObjectBean>> userMapForSyn = new HashMap<>();
            mapForSyn.put(getPlayerID(), userMapForSyn);
            synMap = userMapForSyn;
        }
        String beanName = objectBean.getBeanName();

        ArrayList<ObjectBean> beanList = synMap.get(beanName);
        if (beanList == null) {
            beanList = new ArrayList<ObjectBean>();
            synMap.put(beanName, beanList);
        }
        beanList.add(objectBean);
    }

    //清空同步数据
    private void cleanMapSyn() {
        mapForSyn.remove(getPlayerID());
    }

    //获取同步数据
    public Map<String, ArrayList<ObjectBean>> getMapSyn() {

        //获取之后清空同步数据
        Map<String, ArrayList<ObjectBean>> userMapForSyn = mapForSyn.get(getPlayerID());
        cleanMapSyn();
        return userMapForSyn;
    }
    /////////////////////////////////////【以上向前端同步修改数据】/////////////////////////////////////////////////
    /**
     * 获取玩家ID
     *
     * @return
     */
    public Long getPlayerID() {
        Long player_id = (Long) RequestContextHolder.currentRequestAttributes().getAttribute(Define.PLAYER_ID,
                RequestAttributes.SCOPE_SESSION);
        return player_id;
    }


}
