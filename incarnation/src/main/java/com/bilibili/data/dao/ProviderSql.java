package com.bilibili.data.dao;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xusong
 * @date 2017年9月7日 下午6:13:14
 * @details 外部拼接SQL
 */
public class ProviderSql {

	/**
	 * 获取updateSql
	 * 
	 * @param param
	 * @return updateSql
	 * @throws Exception
	 */
	public String update(Map<String, Object> param) throws Exception {
		String table_name = (String) param.get("table_name");
		Long player_id = (Long) param.get("player_id");
		String key = (String) param.get("key");
		Object value = (Object) param.get("value");
		Map<String, Object> dirty_fields = (HashMap<String, Object>) param.get("dirty_fields");

		StringBuilder sql = new StringBuilder();

		// 更新sql
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("update ").append(table_name).append(" set ");

		for (Map.Entry<String, Object> entry : dirty_fields.entrySet()) {
			String fieldName = entry.getKey();
			Object fieldValue = entry.getValue();
			updateSql.append(fieldName).append(" = '").append(fieldValue).append("',");
		}

		updateSql = updateSql.deleteCharAt(updateSql.length() - 1);
		sql.append(updateSql.toString()).append(" where  player_id = ").append(player_id).append(" and ").append(key)
				.append(" = '").append(value).append("'");
		return sql.toString();
	}

	/**
	 * 获取selectSql
	 * 
	 * @param param
	 * @return selectSql
	 * @throws Exception
	 */
	public String select(Map<String, Object> param) throws Exception {
		String table_name = (String) param.get("table_name"); // 表名
		Long player_id = (Long) param.get("player_id"); // 玩家ID
		String key = (String) param.get("key"); // 对象主键
		Object value = (Object) param.get("value"); // 对象值
		Boolean include_deleted = (Boolean) param.get("include_deleted"); // 是否包含已删除的项 true:包含  false:不包含
		Boolean for_update = (Boolean) param.get("for_update"); // 是否设置for_update

		// 读取sql
		StringBuilder loadSql = new StringBuilder();
		loadSql.append("select * from ").append(table_name).append(" where player_id = ").append(player_id)
				.append(" and ").append(key).append(" = '").append(value).append("'");

		// 是否不包含已经删除的项
		if (!include_deleted) {
			loadSql.append(" and delete_flag = 0");
		}

		loadSql.append(" limit 1");

		// 是否为for_update
		/*if (for_update) {
			loadSql.append(" for update");
		}*/
		return loadSql.toString();
	}

	public String selectList(Map<String, Object> param) throws Exception {
		String table_name = (String) param.get("table_name"); // 表名
		Long player_id = (Long) param.get("player_id"); // 玩家ID

		// 读取sql
		StringBuilder loadSql = new StringBuilder();
		loadSql.append("select * from ").append(table_name).append(" where player_id = ").append(player_id)
				.append(" and delete_flag = 0");
		return loadSql.toString();
	}

	/**
	 * 获取insertSql
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String insert(Map<String, Object> param) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (HashMap<String, Object>) param.get("fieldNameAndValueMap");// fieldName:fieldValue
		String table_name = (String) param.get("table_name"); // 表名

		StringBuilder sql = new StringBuilder();
		StringBuilder insertSqlFirst = new StringBuilder();
		insertSqlFirst.append("insert into ").append(table_name).append(" (");
		StringBuilder insertSqlSecond = new StringBuilder();
		insertSqlSecond.append(") values (");

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String fieldName = entry.getKey();
			Object fieldValue = entry.getValue();
			//暂时过滤掉为null的字段
			if (fieldValue == null){
				continue;
			}
			insertSqlFirst.append(fieldName).append(" ,");
			insertSqlSecond.append("'").append(fieldValue).append("',");
		}

		insertSqlFirst = insertSqlFirst.deleteCharAt(insertSqlFirst.length() - 1);
		insertSqlSecond = insertSqlSecond.deleteCharAt(insertSqlSecond.length() - 1);
		sql.append(insertSqlFirst.toString()).append(insertSqlSecond.toString()).append(" )");
		return sql.toString();
	}

	/**
	 * 获取deleteLogicalSql
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String deleteLogical(Map<String, Object> param) throws Exception {
		String table_name = (String) param.get("table_name");
		Long player_id = (Long) param.get("player_id");
		String key = (String) param.get("key");
		Object value = (Object) param.get("value");

		// 更新sql
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("update ").append(table_name).append(" set delete_flag = 1").append(" where  player_id = ")
				.append(player_id).append(" and ").append(key).append(" = '").append(value).append("'");
		return updateSql.toString();
	}

	/**
	 * 获取insertLogicalSql
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String insertLogical(Map<String, Object> param) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (HashMap<String, Object>) param.get("fieldNameAndValueMap");// fieldName:fieldValue
		Long player_id = (Long) param.get("player_id");
		String key = (String) param.get("key");
		Object value = (Object) param.get("value");
		String table_name = (String) param.get("table_name"); // 表名

		StringBuilder sql = new StringBuilder();

		// 更新sql
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("update ").append(table_name).append(" set ");

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String fieldName = entry.getKey();
			Object fieldValue = entry.getValue();
			updateSql.append(fieldName).append(" = '").append(fieldValue).append("',");
		}

		updateSql = updateSql.append(" delete_flag = 0");
		sql.append(updateSql.toString()).append(" where  player_id = ").append(player_id).append(" and ").append(key)
				.append(" = '").append(value).append("'");
		return sql.toString();
	}
}
