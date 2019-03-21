package com.ibm.extract.enums;

/**
 * @author FuDu
 * @date 2019-03-20
 * @desc 提供 Http 请求相关的配置项
 */
public enum Request {
	GET, 
	POST, 
	OK(200),
	filterCnum("(&(ibmserialnumber=%s)(employeecountrycode=%s))"),
	filterEmail("(emailaddress=%s)"),
	notesMail("notesemail"),
	emailAddress("emailaddress");
	private int code;
	private String value;
	private Request() {}
	private Request(int code) {
		this.code = code;
	}
	private Request(String value) {
		this.value = value;
	}
	public int code() {
		return this.code;
	}
	public String value() {
		return this.value;
	}
}
