package com.freeman.common.log;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;


/**
 * 操作日志记录
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    String title() default "";

    @AliasFor("title")
    String value() default "";


}