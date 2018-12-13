package com.bilibili.bean.master;

import com.bilibili.bean.Bean;
/**
 * @author xusong
 * @date 2018年1月2日 上午10:32:54
 * @details 配置表基类,所有master继承此类
 */
public class MasterBean implements Bean {

	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
