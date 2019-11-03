package com.freeman.job.utils;

import com.freeman.common.utils.SpringContextUtil;
import com.freeman.common.utils.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 执行定时任务
 */
@Slf4j
public class ScheduleRunnable implements Runnable {

    private Object target;
    private Method method;
    private String params;

    ScheduleRunnable(String beanName, String methodName, String params) throws NoSuchMethodException, SecurityException {
        this.target = SpringContextUtil.getBean(beanName);
        this.params = params;

        if (StrUtil.isNotBlank(params)) {
            this.method = target.getClass().getDeclaredMethod(methodName, String.class);
        } else {
            this.method = target.getClass().getDeclaredMethod(methodName);
        }
    }

    @Override
    public void run() {
        try {
            ReflectionUtils.makeAccessible(method);
            if (StrUtil.isNotBlank(params)) {
                method.invoke(target, params);
            } else {
                method.invoke(target);
            }
        } catch (Exception e) {
            log.error("执行定时任务失败", e);
        }
    }

}
