package com.freeman.common.utils;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Bean<-->Bean属性拷贝
 * Bean<-->Map深度转换
 * Bean<-->Bean深度转换
 * 
 * @author 刘志新
 *
 */
public abstract class BeanUtil extends org.springframework.beans.BeanUtils {

	/** 修改spring的BeanUtils,不用null覆盖已有的值 */
	public static void copyProperties(Object source, Object target) throws BeansException {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");
		Class<?> clazz = target.getClass();
		PropertyDescriptor[] targetProps = getPropertyDescriptors(clazz);
		for (PropertyDescriptor targetProp : targetProps) {
			if (targetProp.getWriteMethod() != null) {
				PropertyDescriptor sourceProp = getPropertyDescriptor(source.getClass(), targetProp.getName());
				if (sourceProp != null && sourceProp.getReadMethod() != null) {
					try {
						Method readMethod = sourceProp.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						// 这里判断以下value是否为空 当然这里也能进行一些特殊要求的处理 例如绑定时格式转换等等
						if (value != null) {
							Method writeMethod = targetProp.getWriteMethod();
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}
							writeMethod.invoke(target, value);
						}
					} catch (Throwable ex) {
						throw new FatalBeanException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}


	//==================================Bean<-->Map深度转换========================================//
	/**
	 * 简单封装org.springframework.cglib.beans.BeanMap, 实现Bean<-->Map深度转换
	 */
	/** Bean --> Map */
	public static <T> Map<String, Object> beanToMap(T bean) {
		Map<String, Object> map = new HashMap();
		if (bean != null) {
			BeanMap beanMap = BeanMap.create(bean);

			for (Iterator iterator = beanMap.keySet().iterator(); iterator.hasNext(); ) {
				Object key = iterator.next();
				if (beanMap.get(key) != null) {
					map.put(key.toString(), beanMap.get(key));
				}
			}
		}
		return map;
	}

	/** Map --> Bean */
	public static <T> T mapToBean(Map map, T t) {
		BeanMap beanMap = BeanMap.create(t);
		beanMap.putAll(map);
		return (T)beanMap.getBean();
	}

	/** List<Bean> --> List<Map<String,Objetc>> */
	public static <T> List<Map<String, Object>> beansToMaps(List<T> listbean) {
		List<Map<String, Object>> maps = new ArrayList();
		if (listbean == null || listbean.size() == 0) {
			return null;
		}
		for (T bean : listbean) {
			if (bean != null) {
				Map<String, Object> beanToMaps = beanToMap(bean);
				maps.add(beanToMaps);
			}
		}
		return maps;
	}

	/** List<Map<String,Objetc>> --> List<Bean> */
	public static <T> List<T> mapsToBeans(List<Map<String, Object>> listmap, Class<T> t) {
		List<T> beans = new ArrayList();
		if (listmap == null || listmap.size() == 0) {
			return null;
		}
		for (Map<String, Object> map : listmap) {
			T t1 = null;
			try {
				t1 = t.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			T t2 = mapToBean(map, t1);
			beans.add(t2);
		}
		return beans;
	}


	//==================================Bean<-->Bean深度转换======================================//
	/**
	 * 简单封装Dozer, 实现Bean<-->Bean深度转换
	 * 依赖包 compile "net.sf.dozer:dozer:5.5.1"
	 *
	 * 1. 持有Mapper的单例.
	 * 2. 返回值类型转换.
	 * 3. 批量转换Collection中的所有对象.
	 * 4. 区分创建新的B对象与将对象A值复制到已存在的B对象两种函数.
	 */

	/** 持有Dozer单例, 避免重复创建DozerMapper消耗资源 */
	private static DozerBeanMapper dozer = new DozerBeanMapper();

	/** 基于Dozer将对象A的值拷贝到对象B中 */
	public static void copy(Object source, Object targeObject) {
		dozer.map(source, targeObject);
	}

	/** 基于Dozer转换对象的类型 */
	public static <T> T beanToBean(Object source, Class<T> targeClass) {
		return dozer.map(source, targeClass);
	}

	/** 基于Dozer转换Collection中对象的类型 */
	public static <T> List<T> beansToBeans(Iterable<?> sourceList, Class<T> targeClass) {
		List<T> targeList = new ArrayList();
		for (Object sourceObject : sourceList) {
			T targeObject = dozer.map(sourceObject, targeClass);
			targeList.add(targeObject);
		}
		return targeList;
	}

	/** 基于JDK8,自己实现转换方式 */
	public static <T, S> List<T> beansToBeans(Collection<S> source, Function<? super S, ? extends T> mapper) {
		return source.stream().map(mapper).collect(Collectors.toList());
	}

}
