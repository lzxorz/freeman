package com.freeman.spring.data.hibernate.transformer;

import com.freeman.spring.data.hibernate.transformer.mappedfileds.Fields;
import com.freeman.utils.StringKit;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.BeanUtils;

import java.util.*;


public class AliasToBeanTransformerAdapter<T> implements ResultTransformer {


    private Class<T> mappedClass;
    private Map<String, Fields> mappedFields;
    private ValueSetter valueSetter;
    private MappedFieldsInitializer mappedFieldsInitializer;

    public AliasToBeanTransformerAdapter(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        this.mappedFieldsInitializer = new MappedFieldsInitializer();
        this.mappedFields = mappedFieldsInitializer.init(mappedClass);
        this.valueSetter = new ValueSetter();
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        T mappedObject = BeanUtils.instantiate(mappedClass);

        for (int i = 0; i < aliases.length; i++) {
            String alias = StringKit.underlineToCamel(aliases[i].trim());
            Object value = tuple[i];
            valueSetter.set(mappedObject, alias, value, this.mappedFields);
        }
        valueSetter.clearGenericMap();
        return mappedObject;
    }

    @Override
    public List transformList(List list) {
        return list;
        // if (!mappedFieldsInitializer.isCollection()) {
        //     return list;
        // }
        // return groupCollection(list);
    }

//    @SuppressWarnings("unchecked")
//    private List groupCollection(List srcCollect) {
//        List result = new ArrayList();
//        for (Object a : srcCollect) {
//            boolean existed = false;
//            Object existedObject = null;
//            for (Object b : result) {
//                if (a.equals(b)) {
//                    existed = true;
//                    existedObject = b;
//                }
//            }
//
//            if (existed) {
//                handleExistedObject(existedObject, a);
//            } else {
//                result.add(a);
//            }
//        }
//        return result;
//    }

//    @SuppressWarnings("unchecked")
//    private void handleExistedObject(Object existedObject, Object a) {
//        for (String fieldName : mappedFieldsInitializer.getCollectionFieldNames()) {
//            Collection collection1 = (Collection) ValueSetter.getBeanWrapper(existedObject).getPropertyValue(fieldName);
//            Collection collection2 = (Collection) ValueSetter.getBeanWrapper(a).getPropertyValue(fieldName);
//            Set set = new HashSet();
//
//            for (Object o : collection2) {
//                boolean isEqual = false;
//                for (Object b : collection1) {
//                    if (o.equals(b)) {
//                        isEqual = true;
//                    }
//                }
//                if (!isEqual) {
//                    set.add(o);
//                }
//            }
//            collection1.addAll(set);
//        }
//    }

    public final Class getMappedClass() {
        return this.mappedClass;
    }

}
