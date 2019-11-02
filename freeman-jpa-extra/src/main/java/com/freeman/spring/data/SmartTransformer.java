package com.freeman.spring.data;

import org.hibernate.transform.BasicTransformerAdapter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.List;

public class SmartTransformer extends BasicTransformerAdapter {

    private static DefaultConversionService conversionService = new DefaultConversionService();

    private final Class clazz;

    SmartTransformer(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        if (tuple != null && tuple.length > 0) {
            return conversionService.convert(tuple[0], clazz);
        }
        return null;
    }

    @Override
    public List transformList(List list) {
        return super.transformList(list);
    }
}
