package com.bilibili.data.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * xusong
 * 2017年9月7日 下午6:12:21
 * 通用Dao
 */
@Mapper
@Component
public interface CommonDAO {
    /**
     * 从数据库查询,获取到对应的List
     */
    @SelectProvider(type = ProviderSql.class, method = "select")
    Map<?, ?> select(@Param("table_name") String table_name, @Param("player_id") Long player_id,
                     @Param("key") String key, @Param("value") Object value, @Param("include_deleted") Boolean include_deleted,
                     @Param("for_update") Boolean for_update);

    /**
     * 从数据库查询,获取到对应的对象
     */
    @SelectProvider(type = ProviderSql.class, method = "selectList")
    List<Map<?, ?>> selectList(@Param("table_name") String table_name, @Param("player_id") Long player_id);

    /**
     * 对table表中的数据进行修改(object)
     */
    @UpdateProvider(type = ProviderSql.class, method = "update")
    void update(@Param("table_name") String table_name, @Param("player_id") Long player_id, @Param("key") String key,
                @Param("value") Object value, @Param("dirty_fields") Map<String, Object> dirty_fields);

    /**
     * 对table表中的数据进行逻辑删除（将删除标记位置为1）
     */
    @UpdateProvider(type = ProviderSql.class, method = "deleteLogical")
    void deleteLogical(@Param("table_name") String table_name, @Param("player_id") Long player_id,
                       @Param("key") String key, @Param("value") Object value);

    /**
     * 逻辑插入新数据（将删除标记位置为0）
     */
    @UpdateProvider(type = ProviderSql.class, method = "insertLogical")
    void insertLogical(@Param("table_name") String table_name, @Param("player_id") Long player_id,
                       @Param("key") String key, @Param("value") Object value,
                       @Param("fieldNameAndValueMap") Map<String, Object> map);

    /**
     * 插入新数据
     */
    @InsertProvider(type = ProviderSql.class, method = "insert")
    void insert(@Param("fieldNameAndValueMap") Map<String, Object> map, @Param("table_name") String table_name);

}
