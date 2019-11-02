package com.freeman.common.dataSource;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 控制多数据源访问的Aop
 * @author liuning
 * @time  2019年2月23日 下午8:27:45
 */
@Aspect
@Component
@Order(-100)
public class DynamicDataSourceAspect {

	/** 定义一个切面，拦截所有含有 SalveDbSource的注解 */
	@Pointcut("@annotation(com.freeman.common.dataSource.DbSource)")
    public void dataSourcePointCut() {

    }

	/** 前置通知 */
    /*@Before("dataSourcePointCut()")
    public void before(JoinPoint joinPoint) {
    	// 获取方法上面的SalveDbSource 注解；如果该注解为master=true 则该方法需要走 master库，否则走slave库
    	MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DbSource salveDbSource = method.getAnnotation(DbSource.class);
        if(salveDbSource.master()) {
        	DynamicDataSourceKey.master();
        }else {
        	DynamicDataSourceKey.slave();
        }
    }*/

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 获取方法上面的SalveDbSource 注解；如果该注解为master=true 则该方法需要走 master库，否则走slave库
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        DbSource dbSource = method.getAnnotation(DbSource.class);
        
        if(dbSource.master()) {
            DynamicDataSourceKey.master();
        }else {
            DynamicDataSourceKey.slave();
        }

        try {
            return point.proceed();
        } finally {
            DynamicDataSourceKey.clear();
        }
    }
   
 
}
