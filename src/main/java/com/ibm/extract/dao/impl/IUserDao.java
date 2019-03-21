package com.ibm.extract.dao.impl;

import org.apache.ibatis.annotations.Select;

import com.ibm.extract.model.App;
import com.ibm.extract.model.User;

public interface IUserDao {
	@Select("select * from user where username=#{username}")
	public User getUserByName(String username);
	
	@Select("select a.*, a.app_code as appCode from appl.application a where a.app_code='CSM-CSM'")
	public App getApp();
}
