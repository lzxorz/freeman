/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.freeman.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候取出ApplicaitonContext.
 *
 */
@Slf4j
@Component
public class SpringContextUtil implements ApplicationContextAware, DisposableBean {
	/** 上下文对象实例 */
	private static ApplicationContext applicationContext = null;
	@Autowired @Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtil.applicationContext = applicationContext;
	}

	/** 获取 ApplicationContext */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/** 获取 HttpServletRequest */
	public static HttpServletRequest getHttpServletRequest() {
		return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
	}

	/** 获取 HttpServletResponse */
	public static HttpServletResponse getHttpServletResponse() {
		return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
	}

	/**
	 * 通过name获取 Bean, 自动转型为所赋值对象的类型.
	 * @param name 参数传入要获取的实例的类名 首字母小写
	 * @param <T>
	 * @return
	 */
	public static <T> T getBean(String name) {
		//return (T) applicationContext.getBean(name);
		return (T) getApplicationContext().getBean(name);
	}

	/**
	 * 通过class获取Bean, 自动转型为所赋值对象的类型.
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz){
		return getApplicationContext().getBean(clazz);
	}

	/**
	 * 通过name,Clazz 返回指定的Bean
	 * @param name
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T getBean(String name,Class<T> clazz){
		return getApplicationContext().getBean(name, clazz);
	}

	/**
	 * 发布事件
	 * @param event 事件
	 */
	public static void publishEvent(ApplicationEvent event) {
		if (applicationContext == null) return;
		applicationContext.publishEvent(event);
	}

	/**
	 * 实现DisposableBean接口, 在Context关闭时清理静态变量.
	 */
	@Override
	public void destroy() {
		if (log.isDebugEnabled()) {
			log.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
		}
		applicationContext = null;
	}
}