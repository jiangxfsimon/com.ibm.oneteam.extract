package com.ibm.extract.dao;

import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.ibm.extract.annotation.DataBase;
import com.ibm.extract.constant.DBInfo;
import com.ibm.extract.dao.impl.IAppDao;
import com.ibm.extract.model.App;
import com.ibm.extract.model.Chain;
import com.ibm.extract.model.OrgCode;
@Repository
public class AppDao implements IAppDao{
	@Resource
	private SqlSessionTemplate sqlSession;

	@DataBase(DBInfo.db2)
	@Override
	public List<App> getAllApp() {
		return sqlSession.getMapper(IAppDao.class).getAllApp();
	}
	//specific application
	@DataBase(DBInfo.db2)
	@Override
	public List<String> getRequestsFromFIW() {
		return sqlSession.getMapper(IAppDao.class).getRequestsFromFIW();
	}
	@DataBase(DBInfo.db2)
	@Override
	public List<String> getRequestsFromCDMS() {
		return sqlSession.getMapper(IAppDao.class).getRequestsFromCDMS();
	}
	@DataBase(DBInfo.db2)
	@Override
	public List<String> getRequestsFromWwBalsht() {
		return sqlSession.getMapper(IAppDao.class).getRequestsFromWwBalsht();
	}
	@DataBase(DBInfo.db2)
	@Override
	public List<String> getRequestsFromWWConsol() {
		return sqlSession.getMapper(IAppDao.class).getRequestsFromWWConsol();
	}
	@DataBase(DBInfo.db2)
	@Override
	public List<String> getRequestsFromWWSD() {
		return sqlSession.getMapper(IAppDao.class).getRequestsFromWWSD();
	}

	@DataBase(DBInfo.db2)
	@Override
	public List<String> getRequests(String appCode) {
		return sqlSession.getMapper(IAppDao.class).getRequests(appCode);
	}
	@DataBase(DBInfo.db2)
	@Override
	public List<Chain> getChain(String serialCcs) {
		return sqlSession.getMapper(IAppDao.class).getChain(serialCcs);
	}
	@DataBase(DBInfo.db2)
	@Override
	public List<String> getSpecialEmp() {
		return sqlSession.getMapper(IAppDao.class).getSpecialEmp();
	}
	@DataBase(DBInfo.db2)
	@Override
	public List<Chain> getUpMgrLine(String serialCcs) {
		return sqlSession.getMapper(IAppDao.class).getUpMgrLine(serialCcs);
	}
	@DataBase(DBInfo.db2)
	@Override
	public List<Chain> getUpFunLeaderLine(String serialCcs) {
		return sqlSession.getMapper(IAppDao.class).getUpFunLeaderLine(serialCcs);
	}
	@DataBase(DBInfo.db2)
	@Override
	public List<String> getBpOrgCode() {
		return sqlSession.getMapper(IAppDao.class).getBpOrgCode();
	}
	
	
	
	@DataBase(DBInfo.sqlite)
	@Override
	public int saveInLocalDB(String hrOrgCode, String hrOrgDisplay, String hrUnitId) {
		return sqlSession.getMapper(IAppDao.class).saveInLocalDB(hrOrgCode, hrOrgDisplay, hrUnitId);
	}
	@DataBase(DBInfo.sqlite)
	@Override
	public List<OrgCode> getAllOrgCode() {
		return sqlSession.getMapper(IAppDao.class).getAllOrgCode();
	}
	@DataBase(DBInfo.sqlite)
	@Override
	public void deleteAllOrgCode() {
		 sqlSession.getMapper(IAppDao.class).deleteAllOrgCode();
	}
	

}
