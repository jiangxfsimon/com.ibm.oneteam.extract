package com.ibm.extract.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;


/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 用于动态切换数据库时的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) 
@Documented
public @interface DataBase {
	public String value() default "";
}
