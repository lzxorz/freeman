package com.freeman.common.dataSource;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DbSource {

	@AliasFor("master")
	boolean value() default false;

	/** true: 使用主库, false: 使用从库(默认) */
	@AliasFor("value")
	boolean master() default false;
	
}
