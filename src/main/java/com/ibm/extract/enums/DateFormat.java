package com.ibm.extract.enums;

/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 日期格式的定义
 */
public enum DateFormat {
	dateFormat1("yyyy-MM-dd HH:mm:ss"), dateFormat2("yyyy/MM/dd HH:mm:ss");
	private String value;
	private DateFormat(String value) {
		this.value = value;
	}
	public String value() {
		return this.value;
	}
}
