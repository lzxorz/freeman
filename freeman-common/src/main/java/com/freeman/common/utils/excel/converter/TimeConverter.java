package com.freeman.common.utils.excel.converter;

import com.freeman.common.utils.DateUtil;

import java.util.Date;

/**
 * Execl时间类型字段格式化
 */
public class TimeConverter implements IConverter {

    private String format = "yyyy-MM-dd HH:mm:ss";

    public TimeConverter() {}
    public TimeConverter(String format) {
        this.format = format;
    }

    /**
     * 获取对象值（导入）
     */
    public Object imp(String val) {
        return val == null ? null : DateUtil.parse(val, format);
    }

    /**
     * 获取对象值（导出）
     */
    @Override
    public String exp(Object value) {
        return value == null ? "" : DateUtil.format((Date)value, format);
    }
}
