package com.freeman.spring.data.hibernate.transformer;

import com.freeman.spring.data.hibernate.transformer.mappedfileds.CollectionFields;
import com.freeman.spring.data.hibernate.transformer.mappedfileds.Fields;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.convert.JodaTimeConverters;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ValueSetter {
    private static final String OBJECT_SPLIT = ".";
    protected static DefaultConversionService conversionService;

    private Map<String, Object> genericMap = new HashMap<>();

    static {
        ValueSetter.conversionService = new DefaultConversionService();
        Collection<Converter<?, ?>> convertersToRegister = JodaTimeConverters.getConvertersToRegister();
        for (Converter<?, ?> converter : convertersToRegister) {
            ValueSetter.conversionService.addConverter(converter);
        }
    }

    @SuppressWarnings("unchecked")
    public void set(Object instantiate, String alias, Object value, Map<String, Fields> mappedFields) {
        BeanWrapper beanWrapper = getBeanWrapper(instantiate);
        if (!alias.contains(OBJECT_SPLIT)) {
            Fields field = mappedFields.get(alias);
            Assert.notNull(field, "Can not find field by alias:[" + alias + "]");
            field.setResultPropertyValue(beanWrapper, instantiate, alias, value);
            return;
        }


        int index = alias.indexOf(OBJECT_SPLIT);
        String alias2 = alias.substring(0, index);
        String remainAlias = alias.substring(index + 1);

        Fields field = mappedFields.get(alias2);
        Assert.notNull(field, "Can not find field by alias:[" + alias2 + "]");
        Object propertyValue = beanWrapper.getPropertyValue(field.getName());
        if (propertyValue == null) {
            propertyValue = field.instantiateObjectValue();
            field.setObjectPropertyValue(beanWrapper, propertyValue);
        }

        if (field.isMap()) {
            if (remainAlias.contains(OBJECT_SPLIT)) {
                throw new RuntimeException("Map对象结果只能嵌入一层，不能嵌入多层，alias:" + alias);
            }
            field.setResultPropertyValue(null, propertyValue, remainAlias, value);
            return;
        }

       // if (field.isCollection()) {
       //     CollectionFields collectionFields = (CollectionFields) field;
       //     Object genericValue = getGenericObject(propertyValue, collectionFields);
       //
       //     if (genericValue instanceof Map) {
       //         ((Map) genericValue).put(remainAlias, value);
       //         return;
       //     }
       //
       //     set(genericValue, remainAlias, value, field.getChildrenFields());
       //     return;
       // }

        set(propertyValue, remainAlias, value, field.getChildrenFields());
    }

    @SuppressWarnings("unchecked")
    private Object getGenericObject(Object propertyValue, CollectionFields collectionFields) {
        String name = collectionFields.getName();
        Object o = this.genericMap.get(name);
        if (o != null) {
            return o;
        }
        Collection collection = (Collection) propertyValue;
        Object instantiate = collectionFields.instantiateGenericObject();
        collection.add(instantiate);
        this.genericMap.put(name, instantiate);
        return instantiate;
    }

    public static BeanWrapper getBeanWrapper(Object instantiate) {
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(instantiate);
        bw.setConversionService(conversionService);
        return bw;
    }

    public void clearGenericMap() {
        this.genericMap.clear();
    }

}
