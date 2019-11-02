package com.freeman.spring.data.repository.nativequery;

import com.freeman.common.utils.StrUtil;
import com.freeman.spring.data.repository.specquery.Operator;

import java.util.StringJoiner;

import static com.freeman.spring.data.repository.specquery.Operator.AND;


/**
 * 条件查询构造器
 * @author 刘志新
 */

public class NativeQueryWrapper {

    private String selectSegment = "*";
    private String fromSegment;
    private StringJoiner whereSegment;
    private String groupBySegment;
    private String havingSegment;
    private String orderBySegment;



    /**
     * 默认用 and 连接 查询条件
     */
    public NativeQueryWrapper() { this(AND); }

    /**
     * 指定查询条件之间是用 and 还是 or 连接
     * @param operator Operator.AND/Operator.OR
     */
    public NativeQueryWrapper(Operator operator) {
        this.whereSegment = new StringJoiner(" "+operator.name()+" ");
    }


    /** select 要查询的列 */
    public NativeQueryWrapper select(String columns){
        this.selectSegment = columns;
        return this;
    }

    /** from 要查询的表以及关联表 */
    public NativeQueryWrapper from(String tableAndJoinTable){
        this.fromSegment = tableAndJoinTable;
        return this;
    }

    /** where 条件 */
    public NativeQueryWrapper eq(String columnName, Object value){
        return this.eq(true, columnName,value);
    }
    public NativeQueryWrapper eq(boolean condition, String columnName, Object value){
        if (condition) {
            whereSegment.add(columnName + " = " + value);
        }
        return this;
    }

    public NativeQueryWrapper ne(String columnName, Object value){
        return this.ne(true,columnName,value);
    }
    public NativeQueryWrapper ne(boolean condition, String columnName, Object value){
        if (condition) {
            whereSegment.add(columnName + " != " + value);
        }
        return this;
    }
    
    public NativeQueryWrapper lt(String columnName, Object value){
        return this.lt(true, columnName, value);
    }
    public NativeQueryWrapper lt(boolean condition, String columnName, Object value){
        if (condition) {
            whereSegment.add(columnName + " < " + value);
        }
        return this;
    }

    public NativeQueryWrapper lte(String columnName, Object value){
        return this.lte(true,columnName,value);
    }
    public NativeQueryWrapper lte(boolean condition, String columnName, Object value){
        if (condition) {
            whereSegment.add(columnName + " <= " + value);
        }
        return this;
    }

    public NativeQueryWrapper gt(String columnName, Object value){
        return this.gt(true, columnName, value);
    }
    public NativeQueryWrapper gt(boolean condition, String columnName, Object value){
        if (condition) {
            whereSegment.add(columnName + " > " + value);
        }
        return this;
    }

    public NativeQueryWrapper gte(String columnName, Object value){
        return this.gte(true,columnName,value);
    }
    public NativeQueryWrapper gte(boolean condition, String columnName, Object value){
        if (condition) {
            whereSegment.add(columnName + " >= " + value);
        }
        return this;
    }

    public NativeQueryWrapper isNull(String columnName){
        return this.isNull(true, columnName);
    }
    public NativeQueryWrapper isNull(boolean condition, String columnName){
        if (condition) {
            whereSegment.add(columnName + " IS NULL");
        }
        return this;
    }

    public NativeQueryWrapper isNotNull(String columnName){
        return this.isNotNull(true, columnName);
    }
    public NativeQueryWrapper isNotNull(boolean condition, String columnName){
        if (condition) {
            whereSegment.add(columnName + " IS NOT NULL");
        }
        return this;
    }

    public NativeQueryWrapper like(String columnName, Object value){
        return this.like(true,columnName,value);
    }
    public NativeQueryWrapper like(boolean condition, String columnName, Object value){
        if (condition) {
            whereSegment.add(columnName + " LIKE "+value);
        }
        return this;
    }

    public NativeQueryWrapper notLike(String columnName, Object value) {
        return this.notLike(true, columnName, value);
    }
    public NativeQueryWrapper notLike(boolean condition, String columnName, Object value) {
        if (condition) {
            whereSegment.add(columnName + " NOT LIKE "+value);
        }
        return this;
    }

    public NativeQueryWrapper in(String columnName, Object... value){
        return this.in(true,columnName,value);
    }
    public NativeQueryWrapper in(boolean condition, String columnName, Object... value){
        if (condition) {
            whereSegment.add(columnName + " IN ("+value+")");
        }
        return this;
    }

    public NativeQueryWrapper notIn(String columnName, Object... value){
        return this.notIn(true,columnName,value);
    }
    public NativeQueryWrapper notIn(boolean condition, String columnName, Object... value){
        if (condition) {
            whereSegment.add(columnName + " NOT IN ("+value+")");
        }
        return this;
    }

    public NativeQueryWrapper between(String columnName, Object... value){
        return this.between(true, columnName, value);
    }
    public NativeQueryWrapper between(boolean condition, String columnName, Object... value){
        if (condition) {
            whereSegment.add(columnName + " BETWEEN "+value[0]+" AND "+value[1]);
        }
        return this;
    }

    public NativeQueryWrapper notBetween(String columnName, Object... value){
        return this.notBetween(true, columnName, value);
    }
    public NativeQueryWrapper notBetween(boolean condition, String columnName, Object... value){
        if (condition) {
            whereSegment.add(columnName + " NOT BETWEEN "+value[0]+" AND "+value[1]);
        }
        return this;
    }

    /** groupBy */
    public NativeQueryWrapper groupBy(String groupBySegment){
        this.groupBySegment = groupBySegment;
        return this;
    }
    /** having */
    public NativeQueryWrapper having(String havingSegment){
        this.havingSegment = havingSegment;
        return this;
    }
    /** orderBy */
    public NativeQueryWrapper orderBy(String orderBySegment){
        this.orderBySegment = orderBySegment;
        return this;
    }

    public String build() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add("SELECT " + selectSegment);
        if (StrUtil.isNotBlank(fromSegment)) {
            sj.add("FROM " + fromSegment);
        }
        if (whereSegment.length()>0) {
            sj.add("WHERE " + whereSegment.toString());
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
        // System.out.println("生成sql==> "+sj.toString());
        return sj.toString();
    }
}
