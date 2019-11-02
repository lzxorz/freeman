package com.freeman.spring.data.hibernate.transformer.mappedfileds;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;

import java.beans.PropertyDescriptor;
import java.util.Map;

public class Fields {
    protected String name;
    protected PropertyDescriptor propertyDescriptor;
    protected Map<String, Fields> childrenFields;

    public Fields(PropertyDescriptor propertyDescriptor) {
        this(propertyDescriptor, null);
    }

    public Fields(PropertyDescriptor propertyDescriptor, Map<String, Fields> childrenFields) {
        this.name = propertyDescriptor.getName();
        this.propertyDescriptor = propertyDescriptor;
        this.childrenFields = childrenFields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    public void setPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
    }

    public Map<String, Fields> getChildrenFields() {
        return childrenFields;
    }

    public void setChildrenFields(Map<String, Fields> childrenFields) {
        this.childrenFields = childrenFields;
    }

    public Class<?> getPropertyType() {
        return this.propertyDescriptor.getPropertyType();
    }

    public Object instantiateObjectValue() {
        return BeanUtils.instantiate(getPropertyType());
    }

    public boolean isMap() {
        return false;
    }

    public boolean isCollection() {
        return false;
    }

    public void setResultPropertyValue(BeanWrapper bw, Object instantiate, String name, Object value) {
        if (value != null) {
            bw.setPropertyValue(this.name, value);
        }
    }

    public void setObjectPropertyValue(BeanWrapper bw, Object propertyValue) {
        bw.setPropertyValue(this.name, propertyValue);
    }
}
