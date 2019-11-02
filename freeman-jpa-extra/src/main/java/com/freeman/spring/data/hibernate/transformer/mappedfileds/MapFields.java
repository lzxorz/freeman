package com.freeman.spring.data.hibernate.transformer.mappedfileds;

import org.springframework.beans.BeanWrapper;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

public class MapFields extends Fields {
    public MapFields(PropertyDescriptor propertyDescriptor) {
        super(propertyDescriptor);
    }


    @Override
    public boolean isMap() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setResultPropertyValue(BeanWrapper bw, Object instantiate, String name, Object value) {
        Map map = (Map) instantiate;
        map.put(name, value);
    }

    public Object instantiateObjectValue() {
        return new HashMap<>();
    }


}
