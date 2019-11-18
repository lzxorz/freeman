package com.freeman.spring.data.repository;

import com.freeman.common.utils.DateUtil;
import com.freeman.common.utils.StrUtil;

import java.util.*;



/**
 * 条件查询构造器
 * @author 刘志新
 */

public class NativeSqlQuery {

    private String selectSegment="*";   // 查询的列
    private String fromSegment;         // 表别名 要与 select 中的对应
    private StringJoiner whereSegment;  // where 条件拼接
    private String sqlStrPart ="";      // 追加到 where 条件尾部的自定义sql字符串片段
    private String groupBySegment;      //
    private String havingSegment;       //
    private String orderBySegment;      // nativeSqlQuery.orderBy("字段 asc,字段 desc"). 注意: 使用这个条件查询构造器时,pageable对象中的排序无效。

    public static enum Operator {
        EQ,NE,LT,LTE,GT,GTE,ISNULL,ISNOTNULL,ISEMPTY,ISNOTEMPTY,LIKE,NOTLIKE,IN,NOTIN,BETWEEN,NOTBETWEEN,AND,OR;
    }

    /**
     * 默认用 and 连接 查询条件
     */
    public NativeSqlQuery() { this(Operator.AND); }

    /**
     * 指定查询条件之间是用 and 还是 or 连接
     * @param operator Operator.AND/Operator.OR
     */
    public NativeSqlQuery(Operator operator) {
        this.whereSegment = new StringJoiner(" "+operator.name()+" ");
    }

    /**
     * 快捷构建方法
     */
    public static NativeSqlQuery builder() {
        return new NativeSqlQuery(Operator.AND);
    }

    /** select 要查询的列 */
    public NativeSqlQuery select(String columns){
        this.selectSegment = columns;
        return this;
    }

    /** from 要查询的表以及关联表 */
    public NativeSqlQuery from(String tableAndJoinTable){
        this.fromSegment = tableAndJoinTable;
        return this;
    }

    /** where 条件 begin */
    public NativeSqlQuery eq(String columnName, Object value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " = " + wrappValue(value));
        }
        return this;
    }

    public NativeSqlQuery ne(String columnName, Object value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " != " + wrappValue(value));
        }
        return this;
    }
    
    public NativeSqlQuery lt(String columnName, Object value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " < " + wrappValue(value));
        }
        return this;
    }

    public NativeSqlQuery lte(String columnName, Object value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " <= " + wrappValue(value));
        }
        return this;
    }

    public NativeSqlQuery gt(String columnName, Object value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " > " + wrappValue(value));
        }
        return this;
    }

    public NativeSqlQuery gte(String columnName, Object value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " >= " + wrappValue(value));
        }
        return this;
    }

    public NativeSqlQuery isNull(String columnName){
        whereSegment.add(columnName + " IS NULL");
        return this;
    }

    public NativeSqlQuery isNotNull(String columnName){
        whereSegment.add(columnName + " IS NOT NULL");
        return this;
    }

    /*public NativeSqlQuery like(boolean condition, String columnName, Object value){
        if (condition && Objects.nonNull(value)) {
            whereSegment.add(columnName + " LIKE " + wrappValue(value));
        }
        return this;
    }*/
    public NativeSqlQuery startsWith(String columnName, Object value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " LIKE " + wrappValue(value) + "%");
        }
        return this;
    }
    public NativeSqlQuery contains(String columnName, Object value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " LIKE %" + wrappValue(value) + "%");
        }
        return this;
    }
    public NativeSqlQuery endsWith(String columnName, Object value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " LIKE %" + wrappValue(value));
        }
        return this;
    }
    /*public NativeSqlQuery notLike(boolean condition, String columnName, Object value) {
        if (condition && Objects.nonNull(value)) {
            whereSegment.add(columnName + " NOT LIKE " + wrappValue(value));
        }
        return this;
    }*/
    public NativeSqlQuery notStartsWith(String columnName, Object value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " NOT LIKE " + wrappValue(value) + "%");
        }
        return this;
    }
    public NativeSqlQuery notContains(String columnName, Object value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " NOT LIKE %" + wrappValue(value) + "%");
        }
        return this;
    }
    public NativeSqlQuery notEndsWith(String columnName, Object value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " NOT LIKE %" + wrappValue(value));
        }
        return this;
    }

    public NativeSqlQuery in(String columnName, Object... value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " IN ("+ wrappValue(value) +")");
        }
        return this;
    }

    public NativeSqlQuery notIn(String columnName, Object... value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " NOT IN ("+ wrappValue(value) +")");
        }
        return this;
    }

    public NativeSqlQuery in(String columnName, Collection value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " IN ("+ wrappValue(value) +")");
        }
        return this;
    }

    public NativeSqlQuery notIn(String columnName, Collection value){
        if (Objects.nonNull(value)) {
            whereSegment.add(columnName + " NOT IN ("+ wrappValue(value) +")");
        }
        return this;
    }

    public NativeSqlQuery between(String columnName, Object values){
        if (Objects.nonNull(values)) {
            Object value1,value2;
            Object[] objects = null;
            if (values.getClass().isArray() && ((Object[])values).length == 2) {
                objects = ((Object[])values);
            }  else if (values instanceof Collection && ((Collection)values).size() == 2) {
                objects = ((Collection) values).toArray();
            }
            if (objects != null) {
                value1 = objects[0];
                value2 = objects[1];
                whereSegment.add(columnName + " BETWEEN " + wrappValue(value1) + " AND " + wrappValue(value2));
            }
        }
        return this;
    }

    public NativeSqlQuery notBetween(String columnName, Object values){
        if (Objects.nonNull(values)) {
            Object value1,value2;
            Object[] objects = null;
            if (values.getClass().isArray() && ((Object[])values).length == 2) {
                objects = ((Object[])values);
            }  else if (values instanceof Collection && ((Collection)values).size() == 2) {
                objects = ((Collection) values).toArray();
            }
            if (objects != null) {
                value1 = objects[0];
                value2 = objects[1];
                whereSegment.add(columnName + " NOT BETWEEN " + wrappValue(value1) + " AND " + wrappValue(value2));
            }
        }
        return this;
    }

    /** 追加到 where条件尾部的自定义sql字符串片段 */
    public NativeSqlQuery sqlStrPart(String part){
        this.sqlStrPart = part;
        return this;
    }
    /** where 条件 end */

    /** groupBy */
    public NativeSqlQuery groupBy(String groupBySegment){
        this.groupBySegment = groupBySegment;
        return this;
    }
    /** having */
    public NativeSqlQuery having(String havingSegment){
        this.havingSegment = havingSegment;
        return this;
    }
    /** orderBy */
    public NativeSqlQuery orderBy(String orderBySegment){
        this.orderBySegment = orderBySegment;
        return this;
    }

    /** 这个并没什么用 */
    public NativeSqlQuery build(){
        return this;
    }

    /** 生成完整的sql */
    public String toSqlStr() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add("SELECT " + selectSegment);
        if (StrUtil.isNotBlank(fromSegment)) {
            sj.add("FROM " + fromSegment);
        }
        if (whereSegment.length()>0) {
            sj.add("WHERE " + whereSegment.toString());
            if (StrUtil.isNotBlank(sqlStrPart)) {
                sj.add(sqlStrPart);
            }
        }else if (StrUtil.isNotBlank(sqlStrPart)) {
            sj.add("WHERE " + sqlStrPart);
        }
        if (StrUtil.isNotBlank(groupBySegment)) {
            sj.add("GROUP BY " + groupBySegment);
        }
        if (StrUtil.isNotBlank(havingSegment)) {
            sj.add("HAVING " + havingSegment);
        }
        if (StrUtil.isNotBlank(orderBySegment)) {
            sj.add("ORDER BY " + orderBySegment);
        }
        // System.out.println("生成sql ==> "+sj.toString());
        return sj.toString();
    }


    /** 对值进行包装 */
    private String wrappValue(Object value) {
        String res = "";
        // Class<?> clazz = value.getClass();
        try {
            if (value instanceof String) { // clazz.isPrimitive() || String.class.isAssignableFrom(clazz)
                return  "'"+value+"'";
            } else if (value instanceof Number) { // Number.class.isAssignableFrom(clazz)
                Class<?> c = value.getClass();
                if (c == Integer.class) {
                    res = ""+((Number) value).intValue();
                } else if (c == Long.class) {
                    res = ""+((Number) value).longValue();
                } else if (c == Double.class) {
                    res = ""+((Number) value).doubleValue();
                } else if (c == Float.class) {
                    res = ""+((Number) value).floatValue();
                } else if (c == Short.class) {
                    res = ""+((Number) value).shortValue();
                } else {
                    res = ""+value.toString();
                }
            } else if (value instanceof Boolean) {
                res = value.toString();
            } else if (value instanceof Date) { // Date.class.isAssignableFrom(clazz)
                // "DATE_FORMAT("+ columnName + ",'%Y-%m-%d %H:%i:%s')"
                res = "'"+DateUtil.format((Date)value, "yyyy-MM-dd HH:mm:ss")+"'";
            } else if (value.getClass().isArray()) {
                res = joinArray((Object[])value);
            }  else if (value instanceof Collection) {
                res = joinCollection((Collection)value);
            } /*else if (value instanceof Map) { // Map.class.isAssignableFrom(clazz)
            }*/else if (value != null) {
                res = ""+value.toString();
            }

        } catch (Exception e) {
            throw new RuntimeException("值转换异常 ==> " + value.toString());
        }
        return res;
    }

    private String joinArray(Object[] valueArr) {
        String res = "()";
        if(valueArr.length>0){
            Class<?> type = valueArr.getClass().getComponentType();
            StringJoiner sj = new StringJoiner(",");
            if(type == String.class){
                for (Object v : valueArr) {
                    sj.add("'"+v.toString()+"'");
                }
            }else {
                for (Object v : valueArr) {
                    sj.add(wrappValue(v));
                }
            }
            res = sj.toString();
        }
        return res;
    }

    private String joinCollection(Collection value) {
        String res = "()";
        Iterator iterator =  value.iterator();
        if (iterator.hasNext()){
            StringJoiner sj = new StringJoiner(",");
            while (iterator.hasNext()) {
                Object v = iterator.next();
                if (v instanceof String) {
                    sj.add("'"+v.toString()+"'");
                }else {
                    sj.add(wrappValue(v));
                }
            }
            res =  sj.toString();
        }
        return res;
    }

    /*public Boolean isEmpty(Object v) {
        if (v == null) {
            return true;
        } else if (v instanceof Collection) {
            return ((Collection)v).isEmpty();
        } else if (v instanceof Map) {
            return ((Map)v).isEmpty();
        } else if (v.getClass().isArray()) {
            return Array.getLength(v) == 0;
        } else if (v instanceof Iterator) {
            return !((Iterator)v).hasNext();
        } else if (v instanceof Iterable) {
            return !((Iterable)v).iterator().hasNext();
        } else {
            throw new IllegalArgumentException("isEmpty(...) 方法只能接受 Collection、Map、数组、Iterator、Iterable 类型参数");
        }
    }

    public Boolean notEmpty(Object v) {
        return !this.isEmpty(v);
    }*/
}