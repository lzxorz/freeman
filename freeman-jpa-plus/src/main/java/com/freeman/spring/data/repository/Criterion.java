package com.freeman.spring.data.repository;

import com.freeman.common.utils.StrUtil;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * where或having 的 条件
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
public class Criterion {

    private StringJoiner segments;  // where 条件拼接
    private String sqlStrPart;      // 追加到 where 条件尾部的自定义sql字符串片段
    private int counts = 1;         // 占位符计数器 ?1 ?2 ?3
    private List params;

    public enum Operator {
        EQ,NE,LT,LTE,GT,GTE,ISNULL,ISNOTNULL,ISEMPTY,ISNOTEMPTY,LIKE,NOTLIKE,IN,NOTIN,BETWEEN,NOTBETWEEN,AND,OR;
    }

    /**
     * 默认用 and 连接 查询条件
     */
    public Criterion() { this(Operator.AND); }

    /**
     * 指定查询条件之间是用 and 还是 or 连接
     * @param operator Operator.AND/Operator.OR
     */
    public Criterion(Operator operator) {
        this.segments = new StringJoiner(" "+operator.name()+" ");
        this.params = new ArrayList<>();
    }


    /** where 条件 begin */
    public Criterion eq(String columnName, Object value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} = ?{}", columnName, counts++));
            params.add(value);
        }
        return this;
    }

    public Criterion ne(String columnName, Object value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} != ?{}", columnName, counts++));
            params.add(value);
        }
        return this;
    }

    public Criterion lt(String columnName, Object value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} < ?{}", columnName, counts++));
            params.add(value);
        }
        return this;
    }

    public Criterion lte(String columnName, Object value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} <= ?{}", columnName, counts++));
            params.add(value);
        }
        return this;
    }

    public Criterion gt(String columnName, Object value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} > ?{}", columnName, counts++));
            params.add(value);
        }
        return this;
    }

    public Criterion gte(String columnName, Object value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} >= ?{}", columnName, counts++));
            params.add(value);
        }
        return this;
    }

    public Criterion isNull(String columnName){
        segments.add(columnName + " IS NULL");
        return this;
    }

    public Criterion isNotNull(String columnName){
        segments.add(columnName + " IS NOT NULL");
        return this;
    }

    public Criterion startsWith(String columnName, Object value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} LIKE ?{}", columnName, counts++));
            params.add(value.toString()+"%");
        }
        return this;
    }
    public Criterion contains(String columnName, Object value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} LIKE ?{}", columnName, counts++));
            params.add("%"+value.toString()+"%");
        }
        return this;
    }
    public Criterion endsWith(String columnName, Object value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} LIKE ?{}", columnName, counts++));
            params.add("%"+value.toString());
        }
        return this;
    }

    public Criterion notStartsWith(String columnName, Object value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} NOT LIKE ?{}", columnName, counts++));
            params.add(value.toString()+"%");
        }
        return this;
    }
    public Criterion notContains(String columnName, Object value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} NOT LIKE ?{}", columnName, counts++));
            params.add("%"+value.toString()+"%");
        }
        return this;
    }
    public Criterion notEndsWith(String columnName, Object value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} NOT LIKE ?{}", columnName, counts++));
            params.add("%"+value.toString());
        }
        return this;
    }

    public Criterion in(String columnName, Collection value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} IN ?{}", columnName, counts++));
            params.add(value);
        }
        return this;
    }

    public Criterion notIn(String columnName, Collection value){
        if (Objects.nonNull(value)) {
            segments.add(StrUtil.format("{} NOT IN ?{}", columnName, counts++));
            params.add(value);
        }
        return this;
    }

    public Criterion between(String columnName, List values){
        if (!ObjectUtils.isEmpty(values) && values.size()==2) {
            segments.add(StrUtil.format("{} BETWEEN ?{} AND ?{}", columnName, counts++, counts++));
            params.addAll(values);
        }
        return this;
    }

    public Criterion notBetween(String columnName, List values){
        if (!ObjectUtils.isEmpty(values) && values.size()==2) {
            segments.add(StrUtil.format("{} NOT BETWEEN ?{} AND ?{}", columnName, counts++, counts++));
            params.addAll(values);
        }
        return this;
    }

    /** 追加到 where条件尾部的自定义sql字符串片段 */
    public Criterion sqlStrPart(String part){
        this.sqlStrPart = part;
        return this;
    }


    protected StringJoiner getSegments() {
        return sqlStrPart != null ? segments.add(sqlStrPart) : segments;
    }

    protected int getCounts() {
        return counts-1;
    }

    protected List getParams() {
        return params;
    }
}
