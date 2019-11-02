/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.freeman.common.utils.excel.converter;

import java.util.Map;

/**
 *
 */
public class DictConverter implements IConverter {

    private Map<String,String> labelValueMap; // key: 显示文字, value: 字典值
    private Map<String,String> valueLabelMap; // key: 字典值, value: 显示文字

    public DictConverter() {}
    public DictConverter(Map<String,String> labelValueMap, Map<String,String> valueLabelMap) {
        this.labelValueMap = labelValueMap;
        this.valueLabelMap = valueLabelMap;
    }

    /**
     * 获取对象值（导入）
     */
    @Override
    public Object imp(String label) {
        return label == null ? "" : labelValueMap.get(label);
    }

    /**
     * 获取对象值（导出）
     */
    @Override
    public String exp(Object value) {
        return value == null ? "" : valueLabelMap.get(value);
    }
	
}
