package com.ibm.extract.service.impl;

import com.ibm.extract.model.App;
import com.ibm.extract.model.User;

public interface IUserService {
	public User getUserByName(String username);
	public App getApp();
}
