package com.ibm.extract.dao;

import javax.annotation.Resource;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import com.ibm.extract.annotation.DataBase;
import com.ibm.extract.constant.DBInfo;
import com.ibm.extract.dao.impl.IUserDao;
import com.ibm.extract.model.App;
import com.ibm.extract.model.User;

@Repository
public class UserDao implements IUserDao {
	@Resource
	private SqlSessionTemplate sqlSession;

	@DataBase(DBInfo.sqlite)
	@Override
	public User getUserByName(String username) {
		return sqlSession.getMapper(IUserDao.class).getUserByName(username);
	}

	@DataBase(DBInfo.db2)
	@Override
	public App getApp() {
		return sqlSession.getMapper(IUserDao.class).getApp();
	}

}
