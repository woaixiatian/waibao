package com.bilibili.data.manager;
import com.bilibili.annotation.Table;
import com.bilibili.bean.master.MasterBean;
import com.bilibili.data.dao.MasterDAO;
import com.bilibili.util.Log;
import com.bilibili.util.PackageScanner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author xusong
 * @date 2017年8月21日 下午6:42:51
 * @details 获取配置表信息
 */
@Service
public class MasterManager {

	private MasterDAO masterDao;

	private static Map<Class<?>, ImmutableMap<Integer, Object>> beanMap_MasterMap = new HashMap<Class<?>, ImmutableMap<Integer, Object>>(); // map(tableClass,beanMap<key,master>)
	private static Map<Class<?>, ImmutableList<Object>> beanList_MasterMap = new HashMap<Class<?>, ImmutableList<Object>>(); // map(tableClass,beanList<master>)
	private static Map<String, ImmutableList<Object>> masterAll = new HashMap<String, ImmutableList<Object>>(); //存放所有master
	private static String basePackage = "com.bilibili.bean.master"; // 包路径

	MasterManager(MasterDAO masterDao) {
		this.masterDao = masterDao;
		initMasterAll();
	}

	/**
	 * 获取存储master对象的map
	 * 
	 * @param cls
	 * @return HashMap<Integer, T>
	 */
	@SuppressWarnings("unchecked")
	public <T> Map<Integer, T> getMasterMap(Class<T> cls) {
		Map<Integer, Object> beanMap = beanMap_MasterMap.get(cls);
		return (Map<Integer, T>) beanMap;
	}

	/**
	 * 通过key获取master
	 * 
	 * @param cls
	 * @param key
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public <T> T getMasterFromMapByKey(Class<T> cls, Integer key) {
		try {
			Object obj = beanMap_MasterMap.get(cls).get(key);
			return (T) obj;
		} catch (Exception e) {
			Log.error("表[{}]不存在,或[{}]主键不存在", cls.getSimpleName(), key);
			return null;
		}
	}

	/**
	 * 获取存储master对象的list
	 * 
	 * @param cls
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getMasterList(Class<T> cls) {
		List<Object> beanList = beanList_MasterMap.get(cls);
		return (List<T>)beanList;
	}

	/**
	 * 通过index获取master
	 * 
	 * @param cls
	 * @param index
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public <T> T getMasterFromListByIndex(Class<T> cls, int index) {
		try {
			Object obj = beanList_MasterMap.get(cls).get(index);
			return (T) obj;
		} catch (Exception e) {
			Log.error("表[{}]不存在,或[{}]索引不存在", cls.getSimpleName(), index);
			return null;
		}
	}

	/**
	 * 获取全部master配置信息
	 * @return masterAll
	 */
	public Map<String, ImmutableList<Object>> getMasterAll(){
		return masterAll;
	}
	
	/**
	 * 刷新全部master配置信息
	 */
	public void refreshMasterAll(){
		initMasterAll();
	}
	
	/**
	 * 克隆对象
	 * 
	 * @param obj
	 * @return cloneObj
	 */
	@Deprecated
	@SuppressWarnings("unused")
	private Object copy(Object obj) {
		Object cloneObj = null;
		try {
			// 写入字节流
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream obs = new ObjectOutputStream(out);
			obs.writeObject(obj);
			obs.close();
			// 分配内存，读取原始对象，生成新对象
			ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(ios);
			// 返回生成的新对象
			cloneObj = ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cloneObj;
	}
	
	/**
	 * 初始化全部master配置信息
	 */
	private void initMasterAll(){
		Log.debug("-----------------读取配置信息开始------------------");
		Integer classNum = 0;
		try {
			// 扫描Master包
			Set<Class<?>> clazzs = PackageScanner.findFileClass(basePackage); // 扫描Master包
			for (Class<?> clazz : clazzs) {
				// 获取存在Table注解的class
				Table table = AnnotationUtils.findAnnotation(clazz, Table.class);
				if (table != null) {
					String table_name = table.name();
					// 读取master
					masterAll(clazz, table_name);
					classNum ++;
				}
			}
			
		} catch (Exception e) {
			Log.error("-----------------读取master error------------------",e);
		}
		Log.debug("-----------------共读取\"" + classNum + "\"个配置文件------------------");
		Log.debug("-----------------读取配置信息结束------------------");
	}
	
	/**
	 * 读取master数据并放入内存
	 * 
	 * @param cls
	 * @param table_name
	 * @throws Exception 
	 */
	private <T extends MasterBean> void masterAll(Class<?> cls, String table_name) throws Exception {
		List<Object> objectList = new ArrayList<Object>();
		Map<Integer, Object> objectMap = new HashMap<Integer, Object>();

		Log.debug("-----------------读取[{}]表------------------", table_name);
		List<Map<?, ?>> masterList = masterDao.selectMasterByName(table_name); // 从数据库查询配置表信息
		for (Map<?, ?> map : masterList) {
			T obj = mapToBean(map, cls);
			objectList.add(obj);
			objectMap.put(obj.getId(), obj);
		}
		// 复制成不可变Map
		ImmutableMap<Integer, Object> objectMapImmu = ImmutableMap.copyOf(objectMap);
		beanMap_MasterMap.put(cls, objectMapImmu);
		
		// 复制成不可变List
		ImmutableList<Object> objectListImmu = ImmutableList.copyOf(objectList);
		beanList_MasterMap.put(cls, objectListImmu);
		masterAll.put(cls.getSimpleName(), objectListImmu);
	}

	/**
	 * 将map转成实体类bean
	 */
	@SuppressWarnings("unchecked")
	private <T extends MasterBean> T mapToBean(Map<?, ?> map, Class<?> cls) throws Exception {
		if (map == null) {
			return null;
		}
		T obj = (T) cls.newInstance();
		// 获取实体类的Field
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			int mod = field.getModifiers();
			if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
				continue;
			}
			field.setAccessible(true);
			field.set(obj, map.get(field.getName())); // 赋值
		}
		Integer id = (Integer) map.get("id");
		obj.setId(id);
		return obj;
	}

}
