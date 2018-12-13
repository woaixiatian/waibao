package com.bilibili.config.dynamic_datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * describe :
 * author : xusong
 * createTime : 2018/7/3
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceType();
    }

}