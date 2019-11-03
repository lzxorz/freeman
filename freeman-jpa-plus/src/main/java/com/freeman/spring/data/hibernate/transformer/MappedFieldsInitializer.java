package com.freeman.spring.data.hibernate.transformer;

import com.freeman.spring.data.hibernate.transformer.mappedfileds.Fields;
import com.freeman.spring.data.hibernate.transformer.mappedfileds.MapFields;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class MappedFieldsInitializer {
//  private boolean collection = false;
//  private List<String> collectionFieldNames = new ArrayList<>();


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

            Fields childField = null;
            // if (isCollection(propertyType)) {
            //    this.collection = true;
            //    propertyType = getGenericType(mappedClass, name);
            //    childField = new CollectionFields(pd, propertyType);
            //    collectionFieldNames.add(name);
            // }

            // if (childField == null) {
               childField = new Fields(pd);
            // }

            childField.setChildrenFields(init(propertyType));
            fields.put(name, childField);
        }
        return fields;
    }

    private static Class getGenericType(Class mappedClass, String filedName) {
        try {
            Type type = ((ParameterizedType) mappedClass.getDeclaredField(filedName).getGenericType()).getActualTypeArguments()[0];
            if (type.getTypeName().contains("java.util.Map")) {
                return Map.class;
            }
            return (Class) type;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }



   // public List<String> getCollectionFieldNames() {
   //     return collectionFieldNames;
   // }

    private static boolean isCollection(Class<?> propertyType) {
        return Collection.class.isAssignableFrom(propertyType);
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
