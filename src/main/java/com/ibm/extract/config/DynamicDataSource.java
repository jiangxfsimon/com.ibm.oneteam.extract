package com.ibm.extract.config;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 动态数据源, 用于多个数据源切换
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	public static final ThreadLocal<String> contextHolder = new ThreadLocal<String>(); 
		
	public static void setDBType(String dbType) {
        contextHolder.set(dbType);
    }
 
    public static String getDBType() {
        return contextHolder.get();
    }
 
    public static void clearDBType() {
        contextHolder.remove();
    }
    
    @Override
	protected DataSource determineTargetDataSource() {
    	return super.determineTargetDataSource();
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return getDBType();
	}
	
}
