package com.freeman.common.dataPermission;

import java.lang.annotation.*;


/**
 * 数据规则
 * 加这个注解的字段,表明此属性需要定义(列表页面)查询规则
 *
 */
@Deprecated
@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DataRule {

    /**
     * SQL字段的默认操作符
     * 没有对当前用户的角色定义此字段的查询规则时,使用这个注解属性的值
     */
    Operator defualtOperate() default Operator.EQ;

    /**
     * 表的别名 数据权限使用，应该与模板查询中的别名一致，
     * 字段上不设置值, 默认 从实体类上的注解com.freeman.common.annotation.TableName中取tableAlias值
     */
    String tableAlias() default "";

    /** 数据权限需要控制到列的时候,返回数据前...暂不实现此逻辑 */
    Class<?>[] groups() default {};


}