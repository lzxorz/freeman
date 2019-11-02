package com.freeman.utils;

import org.springframework.context.ApplicationContext;

import java.util.Collection;

public class ApplicationContextHolder {
    private static ApplicationContext appContext;

    public static <T> Collection<T> getBeansOfType(Class<T> clazz) {
        return appContext.getBeansOfType(clazz).values();
    }

    public static <T> T getBean(Class<T> clazz) {
        return appContext.getBean(clazz);
    }


    public static void setAppContext(ApplicationContext appContext) {
        ApplicationContextHolder.appContext = appContext;
    }
}
