package com.ibm.extract.aop;

import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.ibm.extract.annotation.DataBase;
import com.ibm.extract.config.DynamicDataSource;
import com.ibm.extract.constant.DBInfo;
import com.ibm.extract.utils.Utils;

/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 用于拦截所有加了 @DataBase 注解的方法, 方法调用结束前关闭清空数据源
 */
@Component
@Aspect
public class AspectDataBase {
	@Resource
	private Utils utils;
	@Pointcut(DBInfo.dataBasePointCutExpression)
	public void pointCut() { }
	
	public void methodExecuteBefore(JoinPoint jp) {}
	
	@Around("pointCut()")
	public Object methodExecuteAround(ProceedingJoinPoint pjp) throws Throwable {
		MethodSignature signature = (MethodSignature)pjp.getSignature();
		Object target = pjp.getTarget();
		Method method = target.getClass().getMethod(signature.getName(), signature.getParameterTypes());
		DataBase dataBase = method.getAnnotation(DataBase.class);
		if(null != dataBase) {
			DynamicDataSource.setDBType(dataBase.value());
//			System.out.println(utils.dateFormat(DateFormat.dateFormat1.value()) + "当前使用的数据库是: " + dataBase.value());
			Object result = pjp.proceed();
			DynamicDataSource.clearDBType();
			return result;
		} else {
			return pjp.proceed();
		}	
	}
	
	public void methodExecuteAfter(JoinPoint jp) {}
	
}