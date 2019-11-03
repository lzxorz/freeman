package com.freeman.spring.data.hibernate.transformer.mappedfileds;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * 还未完成
 */
public class CollectionFields extends Fields {
    private Class<?> genericClass;

    public CollectionFields(PropertyDescriptor propertyDescriptor, Class<?> genericClass) {
        super(propertyDescriptor);
        this.genericClass = genericClass;
    }

    public boolean isCollection() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setResultPropertyValue(BeanWrapper bw, Object instantiate, String name, Object value) {

    }

    public Object instantiateObjectValue() {
        Class<?> propertyType = this.propertyDescriptor.getPropertyType();
        if (propertyType.isAssignableFrom(Set.class)) {
            return new HashSet<>();
        } else if (propertyType.isAssignableFrom(List.class)) {
            return new ArrayList<>();
        }
        throw new RuntimeException("只支持两种集合类型：Set,List");
    }

    public Class<?> getGenericClass() {
        return genericClass;
    }


    public Object instantiateGenericObject() {
        if (this.genericClass.isAssignableFrom(Map.class)) {
            return new HashMap<>();
        }
        return BeanUtils.instantiate(this.genericClass);
    }

}
