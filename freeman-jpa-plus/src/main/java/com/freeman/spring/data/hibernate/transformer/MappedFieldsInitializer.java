package com.freeman.spring.data.hibernate.transformer;

import com.freeman.spring.data.hibernate.transformer.mappedfileds.Fields;
import com.freeman.spring.data.hibernate.transformer.mappedfileds.MapFields;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class MappedFieldsInitializer {

    public Map<String, Fields> init(Class mappedClass) {
        Map<String, Fields> fields = new HashMap<>();
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() == null) {
                continue;
            }

            Class<?> propertyType = pd.getPropertyType();
            String name = pd.getName();

            if (isPrimitive(propertyType)) {
                fields.put(name, new Fields(pd));
                continue;
            }

            if (isMap(propertyType)) {
                fields.put(name, new MapFields(pd));
                continue;
            }

            Fields childField = new Fields(pd);

            childField.setChildrenFields(init(propertyType));
            fields.put(name, childField);
        }
        return fields;
    }

    private static boolean isMap(Class<?> propertyType) {
        return Map.class.isAssignableFrom(propertyType);
    }

    private static boolean isPrimitive(Class<?> propertyType) {
        return Number.class.isAssignableFrom(propertyType) ||
                propertyType.isPrimitive() ||
                String.class.isAssignableFrom(propertyType) ||
                Date.class.isAssignableFrom(propertyType);
    }

}
