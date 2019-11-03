package com.freeman;

import cn.hutool.core.lang.Console;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.*;
import java.util.Arrays;

/**
 * @author: 刘志新
 * @email: lzxorz@163.com
 * @date: 19-9-5
 * @version: 1.0
 */
public class annotationTest {

    @Target(value = {ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Retention(value = RetentionPolicy.RUNTIME)
    @Inherited // 跟 是否有 继承性 毫无关系
    @interface A {
        String value() default "";
    }

    @Target(value = ElementType.TYPE)
    @Retention(value = RetentionPolicy.RUNTIME)
    // @Inherited // 跟 是否有 继承性 毫无关系
    @interface B {
        String value() default "";
        String name() default "";
    }

    @A // 在C注解上声明 A 注解
    @B // 在C注解上声明 B 注解
    @Target(value = ElementType.TYPE)
    @Retention(value = RetentionPolicy.RUNTIME)
    @interface C {
        String value() default "";
        /**
         * spring提供的注解@AliasFor
         * AliasFor注解用来表示要覆盖B注解中的name()属性方法，
         * annotation属性声明的注解类必须存在于该注解的元注解上
         * attribute属性声明的值必须存在于B注解属性方法中(即B注解的name方法)
         */
        @AliasFor(annotation = B.class, attribute = "name")
        String name() default "cc";
    }


    // 使用C注解的类
    @C("CC")
    class DemoClass {
    }

    public static void main(String[] args) {
        Annotation[] annotations = DemoClass.class.getAnnotations();
        Console.log(Arrays.toString(annotations));
        Console.log("=======================================");

        // spring提供的工具类 AnnotationUtils
        A a1 = AnnotationUtils.findAnnotation(DemoClass.class, A.class);
        B b1 = AnnotationUtils.findAnnotation(DemoClass.class, B.class);
        Console.log("A注解=>{} , B注解=>{}",a1,b1);

        // spring提供的工具类 AnnotatedElementUtils
        A a2 = AnnotatedElementUtils.getMergedAnnotation(DemoClass.class, A.class);
        B b2 = AnnotatedElementUtils.getMergedAnnotation(DemoClass.class, B.class);
        Console.log("A注解=>{} , B注解=>{}",a2,b2);

        // 控制台输出:
        // [@com.freeman.annotationTest$C(name=cc, value=CC)]
        // =======================================
        // A注解=>@com.freeman.annotationTest$A(value=) , B注解=>@com.freeman.annotationTest$B(name=, value=)
        // A注解=>@com.freeman.annotationTest$A(value=) , B注解=>@com.freeman.annotationTest$B(name=cc, value=)
    }

}
