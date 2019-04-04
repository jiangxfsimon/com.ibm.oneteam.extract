package com.ibm.extract.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.ibm.extract.annotation.ExtractConfiguration;
import com.ibm.extract.constant.DBInfo;
import com.ibm.extract.exception.CommonException;

/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 项目核心配置类
 */
@ExtractConfiguration
public class Application implements WebMvcConfigurer {
	@Resource
	private Configure configure;
	
	/* 对 properties 文件读取的支持，否则将无法使用 @value 取值 */
	@Bean 
	public static PropertySourcesPlaceholderConfigurer getProperties() { 
		return new PropertySourcesPlaceholderConfigurer(); 
	}
	
	/* 视图层初始化 */
	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix(configure.getViewPrefix());
		viewResolver.setSuffix(configure.getViewSuffix());
		viewResolver.setViewClass(JstlView.class);
		return viewResolver;
	}
	
	/* 数据源配置开始 */
	/* 配置 sqlite 数据源 */
	@Bean(destroyMethod="close")
	public BasicDataSource getSqliteDataSource() {
		BasicDataSource sqliteDataSource = new BasicDataSource();
		sqliteDataSource.setDriverClassName(configure.getSqliteDriver());
		sqliteDataSource.setUrl(configure.getSqliteUrl());
		return sqliteDataSource;
	}
	
	/* 配置 oneteam db2 数据源*/
	@Bean(destroyMethod="close")
	public BasicDataSource getDB2DataSource() {
		BasicDataSource db2DataSource = new BasicDataSource();
		db2DataSource.setDriverClassName(configure.getDb2Driver());
		db2DataSource.setUrl(configure.getDb2Url());
		db2DataSource.setUsername(configure.getDb2User());
		db2DataSource.setPassword(configure.getDb2Password());
		return db2DataSource;
	}
	
	/* 配置动态数据源 */
	@Bean
	public DynamicDataSource getDynamicDataSource() {
		Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		BasicDataSource sqliteDataSource = getSqliteDataSource();
		BasicDataSource db2DataSource = getDB2DataSource();
		targetDataSources.put(DBInfo.sqlite, sqliteDataSource);
		targetDataSources.put(DBInfo.db2, db2DataSource);
		dynamicDataSource.setTargetDataSources(targetDataSources);
		dynamicDataSource.setDefaultTargetDataSource(sqliteDataSource);
		return dynamicDataSource;
	}
	
	/* 配置 SqlSessionFactory 数据工厂*/
	@Bean
	public SqlSessionFactoryBean getFactoryBean() {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(getDynamicDataSource());
		return factoryBean;
	}
	
	/* 配置 SqlSession 数据调用层 */
	@Bean
	public SqlSessionTemplate getSqlSession() throws Exception {
		SqlSessionFactoryBean factoryBean = getFactoryBean();
		SqlSessionTemplate sqlSession = new SqlSessionTemplate(factoryBean.getObject());
		return sqlSession;
	}
	
	/* Spring 事务支持 */
	@Bean
	public DataSourceTransactionManager getTransaction() {
		DataSourceTransactionManager transaction = new DataSourceTransactionManager();
		DynamicDataSource dynamicDataSource = getDynamicDataSource();
		transaction.setDataSource(dynamicDataSource.determineTargetDataSource());
		return transaction;
	}
	/* 数据源配置完成 */
	
	/* 全局异常配置 */
	@Bean
	public SimpleMappingExceptionResolver globalException() {
		SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
		Properties properties = new Properties();
		properties.setProperty(CommonException.class.getName(), configure.getViewError());
		exceptionResolver.setExceptionMappings(properties);
		return exceptionResolver;
	}
	
	/* 静态文件加载 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(configure.getStaticFilePath() + "**").addResourceLocations(configure.getStaticFilePath());
	}
	
	/* 文件上传配置 */
	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSizePerFile(configure.getMaxUploadSizePerFile());
		multipartResolver.setMaxInMemorySize(configure.getMaxUploadInMemorySize());
		multipartResolver.setDefaultEncoding(configure.getDefaultEncoding());
		multipartResolver.setMaxUploadSize(configure.getMaxUploadSize());
		return multipartResolver;
	}
	
	/* 普通 GET 跳转 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//registry.addViewController("/common/{id}").setViewName(viewName);
		
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Validator getValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		// TODO Auto-generated method stub
		return null;
	}
}
