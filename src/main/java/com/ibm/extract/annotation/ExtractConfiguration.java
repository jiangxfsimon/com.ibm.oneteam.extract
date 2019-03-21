package com.ibm.extract.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.ibm.extract.constant.AppInfo;

/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 项目主配置类注解太多, 整合到此注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) 
@Documented

@Configuration
@EnableWebMvc
//@EnableScheduling
@EnableAspectJAutoProxy
@EnableTransactionManagement 
@ComponentScan(basePackages=AppInfo.basePackage)
@MapperScan(basePackages=AppInfo.mapperBasePackage)
public @interface ExtractConfiguration {
	public String value() default "";
}
