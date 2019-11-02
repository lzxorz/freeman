package com.freeman.common.result;

import com.alibaba.fastjson.serializer.ValueFilter;

public class StringValueFilter implements ValueFilter {
    private static String[] ignoreField = {"istatus","type","delFlag"};
    @Override
    public Object process(Object object, String name, Object value) {

        /*if (value instanceof String && !StrUtil.containsAnyIgnoreCase(name,ignoreField)) {
            value = HtmlUtils.htmlUnescape((String)value);
        }*/
        return value;
    }

    /*public static void main(String[] args) {
        String s = "尼古拉&middot;特斯拉";
    }*/
}
