package com.freeman.spring.data.repository;

import com.freeman.common.utils.StrUtil;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 条件查询构造器
 * @author 刘志新
 */

public class NativeSqlQuery {

    private String select ="*";   // 查询的列
    private String from;          // 表别名 要与 select 中的对应
    private Criterion where;      // where 条件拼接

    private String groupBy;       //
    private Criterion having;     //
    private StringJoiner orderBy; //

    private int  counts = 0;      // 占位符计数器 ?1 ?2 ?3
    private List params;          // 占位符对应的参数值


    /** 快捷构建方法 */
    public static NativeSqlQuery builder() {
        return new NativeSqlQuery();
    }

    /** select 要查询的列 */
    public NativeSqlQuery select(String columns){
        this.select = columns;
        return this;
    }

    /** from 要查询的表以及关联表 */
    public NativeSqlQuery from(String tableAndJoinTable){
        this.from = tableAndJoinTable;
        return this;
    }

    /**
     * where
     * 使用方式: nativeSql.where(w -> w.eq().ne().in())
     */
    public NativeSqlQuery where(UnaryOperator<Criterion> func) {
        this.where = func.apply(new Criterion(Criterion.Operator.AND)); // 条件之间 用 and 连接
        return this;
    }

    /** groupBy */
    public NativeSqlQuery groupBy(String groupBySegment){
        this.groupBy = groupBySegment;
        return this;
    }

    /**
     * having
     * 使用方式: nativeSql.having(h -> h.eq().ne().in())
     * */
    public NativeSqlQuery having(UnaryOperator<Criterion> func){
        this.having = func.apply(new Criterion(Criterion.Operator.AND)); // 条件之间 用 and 连接
        return this;
    }

    /**
     * orderBy
     * 排序参数如果是前端传进来,用QueryRequest接收的 ===> nativeSql.orderBy( queryRequest.getOrderBy(表别名) )
     * 手写逻辑指定排序字段 ==> nativeSql.orderBy("su.age asc")
     */
    public NativeSqlQuery orderBy(String orderBySegment){
        if (StrUtil.isNotBlank(orderBySegment)) {
            if (orderBy == null){
                orderBy = new StringJoiner(","); //  多次调用此方法,用逗号拼接 ==> su.age asc,so.create_time desc
            }
            this.orderBy = this.orderBy.add(orderBySegment);
        }
        return this;
    }

    /** 这个没什么用 */
    public NativeSqlQuery build(){
        return this;
    }

    /** 生成完整的sql */
    protected String toSqlStr() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add("SELECT " + select);
        if (StrUtil.isNotBlank(from)) {
            sj.add("FROM " + from);
        }
        if (where != null) {
            String wh = where.getSegments().toString();
            if (wh.length() > 0) {
                sj.add("WHERE " + wh);
                counts += where.getCounts();
                params = where.getParams();
            }
        }
        if (StrUtil.isNotBlank(groupBy)) {
            sj.add("GROUP BY " + groupBy);
        }

        if (having != null) {
            String hv = having.getSegments().toString();
            if (hv.length() > 0) {
                sj.add("HAVING " + moveCount(hv, counts));
                counts += having.getCounts();
                if (params == null) {
                    params = having.getParams();
                } else {
                    params.addAll(having.getParams());
                }
            }
        }

        if (orderBy != null){
            sj.add(StrUtil.format("ORDER BY {}", orderBy.toString()));
        }
        return sj.toString();
    }



    protected List getParams() {
        return params;
    }

    /** 占位符数字调整 */
    private static String moveCount(String value, int counts) {
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile("\\?\\d{1,2}").matcher(value);

        while (m.find()) {
            m.appendReplacement(sb, "?" + (Integer.parseInt(m.group().substring(1))+counts));
        }
        m.appendTail(sb);
        return sb.toString();
    }


    /** 对值进行包装 */
    // private String wrappValue(Object value) {
    //     String res = "";
    //     // Class<?> clazz = value.getClass();
    //     try {
    //         if (value instanceof String) { // clazz.isPrimitive() || String.class.isAssignableFrom(clazz)
    //             return  "'"+value+"'";
    //         } else if (value instanceof Number) { // Number.class.isAssignableFrom(clazz)
    //             Class<?> c = value.getClass();
    //             if (c == Integer.class) {
    //                 res = ""+((Number) value).intValue();
    //             } else if (c == Long.class) {
    //                 res = ""+((Number) value).longValue();
    //             } else if (c == Double.class) {
    //                 res = ""+((Number) value).doubleValue();
    //             } else if (c == Float.class) {
    //                 res = ""+((Number) value).floatValue();
    //             } else if (c == Short.class) {
    //                 res = ""+((Number) value).shortValue();
    //             } else {
    //                 res = ""+value.toString();
    //             }
    //         } else if (value instanceof Boolean) {
    //             res = value.toString();
    //         } else if (value instanceof Date) { // Date.class.isAssignableFrom(clazz)
    //             // "DATE_FORMAT("+ columnName + ",'%Y-%m-%d %H:%i:%s')"
    //             res = "'"+DateUtil.format((Date)value, "yyyy-MM-dd HH:mm:ss")+"'";
    //         } else if (value.getClass().isArray()) {
    //             res = joinArray((Object[])value);
    //         }  else if (value instanceof Collection) {
    //             res = joinCollection((Collection)value);
    //         } /*else if (value instanceof Map) { // Map.class.isAssignableFrom(clazz)
    //         }*/else if (value != null) {
    //             res = ""+value.toString();
    //         }
    //
    //     } catch (Exception e) {
    //         throw new RuntimeException("值转换异常 ==> " + value.toString());
    //     }
    //     return res;
    // }

    // private String joinArray(Object[] valueArr) {
    //     String res = "()";
    //     if(valueArr.length>0){
    //         Class<?> type = valueArr.getClass().getComponentType();
    //         StringJoiner sj = new StringJoiner(",");
    //         if(type == String.class){
    //             for (Object v : valueArr) {
    //                 sj.add("'"+v.toString()+"'");
    //             }
    //         }else {
    //             for (Object v : valueArr) {
    //                 sj.add(wrappValue(v));
    //             }
    //         }
    //         res = sj.toString();
    //     }
    //     return res;
    // }
    //
    // private String joinCollection(Collection value) {
    //     String res = "()";
    //     Iterator iterator =  value.iterator();
    //     if (iterator.hasNext()){
    //         StringJoiner sj = new StringJoiner(",");
    //         while (iterator.hasNext()) {
    //             Object v = iterator.next();
    //             if (v instanceof String) {
    //                 sj.add("'"+v.toString()+"'");
    //             }else {
    //                 sj.add(wrappValue(v));
    //             }
    //         }
    //         res =  sj.toString();
    //     }
    //     return res;
    // }

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

    /*public static void main(String[] args) {
        int c = 12;
        String sql = "HAVING u.username = ?1 AND u.nickname LIKE ?2 AND u.age BETWEEN ?3 AND ?4 ";
        String s = moveCount(sql, c);
        System.out.println(s);
    }*/
}
