package com.freeman.common.result;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Map;


/**
 * <p>枚举类异常断言，提供简便的方式判断条件，并在条件满足时抛出异常</p>
 * <p>错误码和错误信息定义在枚举类中，在本断言方法中，传递错误信息需要的参数</p>
 * https://www.javazhiyin.com/40384.html
 * https://elim.iteye.com/blog/2392583
 * https://my.oschina.net/chkui/blog/1923011
 */
public interface Assert {

    /** 获取返回码 */
    int getCode();

    /** 获取返回信息 */
    String getMessage();

    /** 构建异常对象 */
    FMException builderException(Object... args);

    /**
     * 构建异常对象 */
    FMException builderException(Throwable t, Object... args);


    /** 不是true, 抛异常　*/
    default void isTrue(boolean expression) {
        if (!expression) {
            throw builderException();
        }
    }
    /**
     * 不是true, 抛异常
     *
     * @param expression 待判断布尔变量
     * @param args message占位符对应的参数列表
     */
    default void isTrue(boolean expression, Object... args) {
        if (!expression) {
            throw builderException(args);
        }
    }

    /** 如果值为true, 抛出异常 */
    default void isFalse(boolean expression) {
        if (expression) {
            throw builderException();
        }
    }

    /**
     * 如果值为true, 抛出异常
     *
     * @param expression 待判断布尔变量
     * @param args message占位符对应的参数列表
     */
    default void isFalse(boolean expression, Object... args) {
        if (expression) {
            throw builderException(args);
        }
    }


    /** 对象不是null抛异常 */
    default void isNull(@Nullable Object obj) {
        if (obj != null) {
            throw builderException();
        }
    }

    /**
     * 对象如果不是null抛异常
     *
     * @param obj 待判断的对象
     * @param args message占位符对应的参数列表
     */
    default void isNull(@Nullable Object obj, Object... args) {
        if (obj != null) {
            throw builderException(args);
        }
    }

    /** 对象如果是null 抛出异常 */
    default void notNull(@Nullable Object obj) {
        if (obj == null) {
            throw builderException();
        }
    }
    /**
     * 对象如果是null 抛出异常
     *
     *　@param obj 待判断的对象
     * @param args message占位符对应的参数列表
     */
    default void notNull(@Nullable Object obj, Object... args) {
        if (obj == null) {
            throw builderException(args);
        }
    }

    /** 字符串为空 抛出异常 */
    default void notEmpty(@Nullable String obj) {
        if (null==obj || "".equals(obj)) {
            throw builderException();
        }
    }
    /**
     * 字符串为空 抛出异常
     *
     * @param obj 待判断的对象
     * @param args message占位符对应的参数列表
     */
    default void notEmpty(@Nullable String obj, Object... args) {
        if (null==obj || "".equals(obj)) {
            throw builderException(args);
        }
    }

    /** 为空 抛出异常 */
    default void notEmpty(@Nullable Collection<?> collection) {
        if (collection ==  null || collection.isEmpty()) {
            throw builderException();
        }
    }
    /**
     * 为空 抛出异常
     *
     * @param collection 待判断的对象
     * @param args message占位符对应的参数列表
     */
    default void notEmpty(@Nullable Collection<?> collection, Object... args) {
        if (collection ==  null || collection.isEmpty()) {
            throw builderException(args);
        }
    }

    /** 为空 抛出异常 */
    default void notEmpty(@Nullable Map<?, ?> map) {
        if (map ==  null || map.isEmpty()) {
            throw builderException();
        }
    }
    /**
     * 为空 抛出异常
     *
     * @param map 待判断的对象
     * @param args message占位符对应的参数列表
     */
    default void notEmpty(@Nullable Map<?, ?> map, Object... args) {
        if (map ==  null || map.isEmpty()) {
            throw builderException(args);
        }
    }

    /** 为空 抛出异常 */
    default void notEmpty(@Nullable Object[] array) {
        if (array == null || array.length == 0) {
            throw builderException();
        }
    }

    /**
     * 为空 抛出异常
     *
     * @param array 待判断的对象
     * @param args message占位符对应的参数列表
     */
    default void notEmpty(@Nullable Object[] array, Object... args) {
        if (array == null || array.length == 0) {
            throw builderException(args);
        }
    }



    /**
     * <p>直接抛出异常
     *
     */
    default void fail() {
        throw builderException();
    }

    /**
     * <p>直接抛出异常
     *
     * @param args message占位符对应的参数列表
     */
    default void fail(Object... args) {
        throw builderException(args);
    }

    /**
     * <p>直接抛出异常，并包含原异常信息
     * <p>当捕获非运行时异常（非继承{@link RuntimeException}）时，并该异常进行业务描述时，
     * 必须传递原始异常，作为新异常的cause
     *
     * @param t 原始异常
     */
    default void fail(Throwable t) {
        throw builderException(t);
    }

    /**
     * <p>直接抛出异常，并包含原异常信息
     * <p>当捕获非运行时异常（非继承{@link RuntimeException}）时，并该异常进行业务描述时，
     * 必须传递原始异常，作为新异常的cause
     *
     * @param t 原始异常
     * @param args message占位符对应的参数列表
     */
    default void fail(Throwable t, Object... args) {
        throw builderException(t, args);
    }

    /**
     * <p>断言对象<code>o1</code>与对象<code>o2</code>相等，此处的相等指（o1.equals(o2)为true）。
     * 如果两对象不相等，则抛出异常
     *
     * @param o1 待判断对象，若<code>o1</code>为null，也当作不相等处理
     * @param o2  待判断对象
     */
    default void equals(Object o1, Object o2) {
        if (o1 == o2) {
            return;
        }
        if (o1 == null) {
            throw builderException();
        }
        if (!o1.equals(o2)) {
            throw builderException();
        }
    }

    /**
     * <p>断言对象<code>o1</code>与对象<code>o2</code>相等，此处的相等指（o1.equals(o2)为true）。
     * 如果两对象不相等，则抛出异常
     *
     * @param o1 待判断对象，若<code>o1</code>为null，也当作不相等处理
     * @param o2  待判断对象
     * @param args message占位符对应的参数列表
     */
    default void equals(Object o1, Object o2, Object... args) {
        if (o1 == o2) {
            return;
        }
        if (o1 == null) {
            throw builderException(args);
        }
        if (!o1.equals(o2)) {
            throw builderException(args);
        }
    }

}
