package com.freeman.common.dataPermission;

import com.alibaba.fastjson.JSONArray;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Deprecated
public class DateRuleDto implements Serializable {

    private static final long serialVersionUID = 952016631229191658L;

    private String filedName;
    private String javaType;
    private String columnName;
    private Operator operator;
    private Object value;


    public String getFiledName() {
        return filedName;
    }
    public DateRuleDto setFiledName(String filedName) {
        this.filedName = filedName;
        return this;
    }

    public String getColumnName() {
        return columnName;
    }
    public DateRuleDto setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public String getJavaType() {
        return javaType;
    }
    public DateRuleDto setJavaType(String javaType) {
        this.javaType = javaType;
        return this;
    }

    public Operator getOperator() {
        return operator;
    }
    public DateRuleDto setOperator(Operator operator) {
        this.operator = operator;
        return this;
    }

    public Object getValue() {
        return value;
    }
    public DateRuleDto setValue(Object value) {
        if (value == null){
            return this;
        }
        if (value instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) value;
            Object o = jsonArray.get(0);
            if (o instanceof String) {
                this.value = jsonArray.toArray(new String[jsonArray.size()]);
            } else if (o instanceof BigInteger) {
                this.value = jsonArray.toArray(new BigInteger[jsonArray.size()]);
            } else if (o instanceof Integer) {
                this.value = jsonArray.toArray(new Integer[jsonArray.size()]);
            } else if (o instanceof Long) {
                this.value = jsonArray.toArray(new Long[jsonArray.size()]);
            } else if (o instanceof BigDecimal) {
                this.value = jsonArray.toArray(new BigDecimal[jsonArray.size()]);
            } else if (o instanceof Double) {
                this.value = jsonArray.toArray(new Double[jsonArray.size()]);
            } else if (o instanceof Float) {
                this.value = jsonArray.toArray(new Float[jsonArray.size()]);
            }
        }else {
            this.value = value;
        }
        return this;
    }
}
