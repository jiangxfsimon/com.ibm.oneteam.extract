package com.ibm.extract.constant;

/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 数据库常量
 */
public class DBInfo {
	public final static String sqlite = "sqlite";
	public final static String db2 = "db2";
	public final static String dataBasePointCutExpression = "execution(* " + AppInfo.basePackage + ".dao.*.*(..))";
}
