package com.freeman.spring.data.hibernate.transformer;

import cn.hutool.core.util.StrUtil;
import com.freeman.spring.data.hibernate.transformer.mappedfileds.Fields;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.BeanUtils;

import java.util.*;


public class AliasToBeanTransformer<T> implements ResultTransformer {


    private Class<T> mappedClass;
    private Map<String, Fields> mappedFields;
    private ValueSetter valueSetter;
    private MappedFieldsInitializer mappedFieldsInitializer;

    public AliasToBeanTransformer(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        this.mappedFieldsInitializer = new MappedFieldsInitializer();
        this.mappedFields = mappedFieldsInitializer.init(mappedClass);
        this.valueSetter = new ValueSetter();
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        T mappedObject = BeanUtils.instantiateClass(mappedClass);

        for (int i = 0; i < aliases.length; i++) {
            String alias = StrUtil.toCamelCase(aliases[i].trim());
            Object value = tuple[i];
            valueSetter.set(mappedObject, alias, value, this.mappedFields);
        }
        valueSetter.clearGenericMap();
        return mappedObject;
    }

    @Override
    public List transformList(List list) {
        return list;
    }

    public final Class getMappedClass() {
        return this.mappedClass;
    }

}
