package com.bilibili.data.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author xusong
 * @date 2017年8月22日 下午4:20:56
 * @details 读取master
 */
@Mapper
public interface MasterDAO {
	
	/**
	 * 查询配置信息
	 * @param table_name
	 * @return table_info
	 */
	@Select("select * from ${table_name} where del_flg = '0'")
	public List<Map<?,?>> selectMasterByName(@Param("table_name") String table_name);
}
