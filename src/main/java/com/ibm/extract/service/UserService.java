package com.ibm.extract.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.extract.dao.impl.IUserDao;
import com.ibm.extract.model.App;
import com.ibm.extract.model.User;
import com.ibm.extract.service.impl.IUserService;

@Service
@Transactional
public class UserService implements IUserService {
	@Resource
	private IUserDao userDao;

	@Override
	public User getUserByName(String username) {
		return userDao.getUserByName(username);
	}

	@Override
	public App getApp() {
		return userDao.getApp();
	}

}
