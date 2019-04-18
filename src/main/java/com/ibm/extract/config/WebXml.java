package com.ibm.extract.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author FuDu
 * @date 2019-03-19
 * @desc 相当于 web.xml 配置文件, 用于注册 springMVC
 */
public class WebXml implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext context) throws ServletException {
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(Application.class);
		ctx.setServletContext(context);
		
		Dynamic servlet = context.addServlet("dispatcher", new DispatcherServlet(ctx));
		servlet.addMapping("/");
		servlet.setLoadOnStartup(1);
		
		/*
		 * javax.servlet.FilterRegistration.Dynamic charcterFilter =
		 * context.addFilter("encodingFilter", CharacterEncodingFilter.class);
		 * charcterFilter.setInitParameter("encoding", "UTF-8");
		 * servlet.addMapping("/"); servlet.setLoadOnStartup(2);
		 */
	}

}
