package com.ibm.extract.model;

public class App {
	private String appCode;
	private String name;

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "App [appCode=" + appCode + ", name=" + name + "]";
	}

}
