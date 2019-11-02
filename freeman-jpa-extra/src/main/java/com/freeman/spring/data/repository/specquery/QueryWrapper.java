package com.freeman.spring.data.repository.specquery;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static com.freeman.spring.data.repository.specquery.Operator.AND;
import static com.freeman.spring.data.repository.specquery.Operator.OR;


/**
 * 条件查询构造器(参考Mybatis Plus)
 * @author 刘志新
 */

public class QueryWrapper<T> implements Specification<T>{

    private List<Specification> specificationList;
    private Operator operator;

    /**
     * 默认用 and 连接 查询条件
     */
    public QueryWrapper() { this(AND); }

    /**
     * 指定查询条件之间是用 and 还是 or 连接
     * @param operator Operator.AND/Operator.OR
     */
    public QueryWrapper(Operator operator) {
        this.specificationList = new ArrayList<>();
        this.operator = operator;
    }
    
    
    public QueryWrapper<T> eq(String propertyName,Object value){
        return this.eq(true, propertyName,value);
    }
    public QueryWrapper<T> eq(boolean condition, String propertyName,Object value){
        return this.predicate(condition, new Criterion(propertyName,Operator.EQ,value));
    }

    public QueryWrapper<T> ne(String propertyName,Object value){
        return this.ne(true,propertyName,value);
    }
    public QueryWrapper<T> ne(boolean condition, String propertyName,Object value){
        return this.predicate(condition, new Criterion(propertyName,Operator.NE,value));
    }
    
    public QueryWrapper<T> lt(String propertyName,Object value){
        return this.lt(true, propertyName, value);
    }
    public QueryWrapper<T> lt(boolean condition, String propertyName,Object value){
        return this.predicate(condition, new Criterion(propertyName,Operator.LT,value));
    }

    public QueryWrapper<T> lte(String propertyName,Object value){
        return this.lte(true,propertyName,value);
    }
    public QueryWrapper<T> lte(boolean condition, String propertyName,Object value){
        return this.predicate(condition, new Criterion(propertyName,Operator.LTE,value));
    }

    public QueryWrapper<T> gt(String propertyName,Object value){
        return this.gt(true, propertyName, value);
    }
    public QueryWrapper<T> gt(boolean condition, String propertyName,Object value){
        return this.predicate(condition, new Criterion(propertyName,Operator.GT,value));
    }

    public QueryWrapper<T> gte(String propertyName,Object value){
        return this.gte(true,propertyName,value);
    }
    public QueryWrapper<T> gte(boolean condition, String propertyName,Object value){
        return this.predicate(condition, new Criterion(propertyName,Operator.GTE,value));
    }

    public QueryWrapper<T> isNull(String propertyName){
        return this.isNull(true, propertyName);
    }
    public QueryWrapper<T> isNull(boolean condition, String propertyName){
        return this.predicate(condition, new Criterion(propertyName, Operator.ISNULL, (Object)null));
    }

    public QueryWrapper<T> isNotNull(String propertyName){
        return this.isNotNull(true, propertyName);
    }
    public QueryWrapper<T> isNotNull(boolean condition, String propertyName){
        return this.predicate(condition, new Criterion(propertyName, Operator.ISNOTNULL, (Object)null));
    }

    public QueryWrapper<T> isEmpty(String propertyName){
        return this.isEmpty(true, propertyName);
    }
    public QueryWrapper<T> isEmpty(boolean condition, String propertyName){
        return this.predicate(condition, new Criterion(propertyName, Operator.ISEMPTY, (Object)null));
    }

    public QueryWrapper<T> isNotEmpty(String propertyName){
        return this.isNotEmpty(true, propertyName);
    }
    public QueryWrapper<T> isNotEmpty(boolean condition, String propertyName){
        return this.predicate(condition, new Criterion(propertyName, Operator.ISNOTEMPTY, (Object)null));
    }

    public QueryWrapper<T> like(String propertyName,Object value){
        return this.like(true,propertyName,value);
    }
    public QueryWrapper<T> like(boolean condition, String propertyName,Object value){
        return this.predicate(condition, new Criterion(propertyName,Operator.LIKE,value));
    }

    public QueryWrapper<T> notLike(String propertyName,Object value) {
        return this.notLike(true, propertyName, value);
    }
    public QueryWrapper<T> notLike(boolean condition, String propertyName, Object value) {
        return this.predicate(condition, new Criterion(propertyName,Operator.NOTLIKE,value));
    }

    public QueryWrapper<T> in(String propertyName,Object... value){
        return this.in(true,propertyName,value);
    }
    public QueryWrapper<T> in(boolean condition, String propertyName,Object... value){
        return this.predicate(condition, new Criterion(propertyName,Operator.IN,value));
    }

    public QueryWrapper<T> notIn(String propertyName,Object... value){
        return this.notIn(true,propertyName,value);
    }
    public QueryWrapper<T> notIn(boolean condition, String propertyName,Object... value){
        return this.predicate(condition, new Criterion(propertyName,Operator.NOTIN,value));
    }

    public QueryWrapper<T> between(String propertyName,Object... value){
        return this.between(true, propertyName, value);
    }
    public QueryWrapper<T> between(boolean condition, String propertyName,Object... value){
        return this.predicate(condition, new Criterion(propertyName,Operator.BETWEEN,value));
    }

    public QueryWrapper<T> notBetween(String propertyName,Object... value){
        return this.notBetween(true, propertyName, value);
    }
    public QueryWrapper<T> notBetween(boolean condition, String propertyName,Object... value){
        return this.predicate(condition, new Criterion(propertyName, Operator.NOTBETWEEN, value));
    }

    public QueryWrapper<T> predicate(Specification specification) {
        return predicate(true, specification);
    }

    public QueryWrapper<T> predicate(boolean condition, Specification specification) {
        if (condition) {
            this.specificationList.add(specification);
        }
        return this;
    }

    /**
     * 嵌套的条件之间 用 and 连接
     * @param func
     * @return
     */
    public QueryWrapper<T> and(UnaryOperator<QueryWrapper> func) {
        QueryWrapper child = func.apply(new QueryWrapper(AND));
        this.specificationList.add(child.getPredicate());
        return this;
    }

    /**
     * 嵌套的条件之间 用 or 连接
     * @param func
     * @return
     */
    public QueryWrapper<T> or(UnaryOperator<QueryWrapper> func) {
        QueryWrapper child = func.apply(new QueryWrapper(OR));
        this.specificationList.add(child.getPredicate());
        return this;
    }

    /** 内部 调用 */
    private Specification<T> getPredicate() { return (r,q,b) -> getPredicate(r,q,b); }
    /** 内部 调用 */
    private Predicate getPredicate(Root<T> r, CriteriaQuery<?> q, CriteriaBuilder b) {
        Predicate[] predicates = new Predicate[specificationList.size()];
        for (int i = 0; i < specificationList.size(); i++) {
            predicates[i] = specificationList.get(i).toPredicate(r, q, b);
        }
        if (ObjectUtils.isEmpty(predicates)) return null;

        return AND.equals(operator) ? b.and(predicates) : b.or(predicates);
    }

    /** jpa 调用 */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return getPredicate(root, criteriaQuery, criteriaBuilder);
    }

}
