package com.freeman.spring.data.annotation;

import java.lang.annotation.*;


/**
 * 表别名
 *
 * @author 刘志新
 * @email  lzxorz@163.com
 */

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TableAlias {

    /** 表的别名 数据权限使用,应该与模板查询中的别名一致, 默认: a */
    String value() default "a";

}