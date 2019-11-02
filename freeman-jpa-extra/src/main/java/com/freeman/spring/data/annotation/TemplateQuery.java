package com.freeman.spring.data.annotation;

import com.freeman.spring.data.hibernate.transformer.AliasToBeanTransformerAdapter;
import org.springframework.data.annotation.QueryAnnotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@QueryAnnotation
@Documented
public @interface TemplateQuery {
    String value() default "";

    Class<?> resultTransformer() default AliasToBeanTransformerAdapter.class;  //设置返回ResultTransformer
}
