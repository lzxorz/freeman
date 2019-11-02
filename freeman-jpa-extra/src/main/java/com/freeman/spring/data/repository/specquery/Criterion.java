package com.freeman.spring.data.repository.specquery;

import cn.hutool.core.convert.Convert;
import com.freeman.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.*;
import java.util.Date;

/**
 * 标准查询条件构造
 * @author 刘志新
 * @email  lzxorz@163.com
 */
@Slf4j
public class Criterion<T> implements Specification<T>/*, Serializable*/ {

    private final String propertyName; // 属性名
    private final Operator operator; // 计算符
    private final Object[] value; // 对应值

    public Criterion(String propertyName,Operator operator, Object... value) {
        this.propertyName = propertyName;
        this.operator = operator;
        this.value = value;
    }


    /**
     * 构建的 Predicate 中的 Path
     * 不是 Path path = root.get("idCard.area.name");
     * 而是 Path path = ( (Path<Area>) ((Path<SysOrg>)root.get("idCard")).get("area") ).get("name");
     *
     * @param propertyName 属性名称,支持级联属性
     */
    private <T> Path getPath(Root<T> root, String propertyName) {

        if (propertyName.contains(".")) {
            String[] names = propertyName.split("\\.");
            return root.join(names[0], JoinType.LEFT).get(names[1]);

            /*Path<T> path = root;
            for (int i = 0; i < names.length; i++) {
                path = path.get(names[i]);
            }
            ////
            Map<String, Join<T, T>> map = new HashMap<>();
            Path path = null;
            for (int i = 0; i < names.length; i++) {
                if (i==0) {
                    Join<T, T> join = root.join(names[i], JoinType.LEFT);
                    map.put(names[i], join);
                } else if (i<names.length-1) {
                    Join<T, T> join = map.get(names[i-1]).join(names[i], JoinType.LEFT);
                    map.put(names[i], join);
                }
                if (i>0) {
                    path = map.get(names[i-1]).get(names[i]);
                }
            }
            return path;*/
        }
        return root.get(propertyName);
    }


    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        if(ObjectUtils.isEmpty(value) && !operator.equals(Operator.ISNULL) && !operator.equals(Operator.ISNOTNULL))
            return null;


        Predicate predicate = null;
        switch (operator){
            case EQ:
                predicate = builder.equal(getPath(root,propertyName),value[0]); break;
            case NE:
                predicate = builder.notEqual(getPath(root,propertyName),value[0]); break;
            case LT:
                predicate = builder.lessThan(getPath(root,propertyName),(Comparable) value[0]); break;
            case LTE:
                predicate = builder.lessThanOrEqualTo(getPath(root,propertyName),(Comparable) value[0]); break;
            case GT:
                predicate = builder.greaterThan(getPath(root,propertyName),(Comparable) value[0]); break;
            case GTE:
                predicate = builder.greaterThanOrEqualTo(getPath(root,propertyName),(Comparable) value[0]); break;
            case ISNULL:
                predicate = builder.isNull(getPath(root,propertyName)); break;
            case ISNOTNULL:
                predicate = builder.isNotNull(getPath(root,propertyName)); break;
            case ISEMPTY:
                predicate = builder.isEmpty(getPath(root,propertyName)); break;
            case ISNOTEMPTY:
                predicate = builder.isNotEmpty(getPath(root,propertyName)); break;
            case LIKE:
                predicate = builder.like(getPath(root,propertyName),(String) value[0]); break;
            case NOTLIKE:
                predicate = builder.like(getPath(root,propertyName),(String) value[0]).not(); break;
            case IN:
                Object[] oArrIn = (value.length == 1 && value[0].getClass().isArray()) ? (Object[])value[0] : value;
                predicate = getPath(root,propertyName).in(oArrIn);
                break;
            case NOTIN:
                Object[] oArrNotin = (value.length == 1 && value[0].getClass().isArray()) ? (Object[])value[0] : value;
                predicate = getPath(root,propertyName).in(oArrNotin).not();
                break;
            case BETWEEN:
                Object[] oArrBetween = (value.length == 1 && value[0].getClass().isArray()) ? (Object[])value[0] : value;
                if (oArrBetween.length == 2){
                    Class javaType = getPath(root,propertyName).getJavaType();
                    if (Date.class.equals(javaType)){
                        if (oArrBetween[0] instanceof String){
                            predicate = builder.between(getPath(root,propertyName),((Comparable) DateUtil.parse((String)oArrBetween[0])),((Comparable) DateUtil.parseDateTimeOrEndOfDay((String)oArrBetween[1])));
                        }else if(oArrBetween[0] instanceof Long){
                            predicate = builder.between(getPath(root,propertyName),((Comparable)new Date(Convert.toLong(oArrBetween[0]))),((Comparable)new Date(Convert.toLong(oArrBetween[0]))));
                        }else /*if(oArrBetween[0] instanceof Date)*/{
                            predicate = builder.between(getPath(root,propertyName),((Comparable)oArrBetween[0]),((Comparable)oArrBetween[1]));
                        }
                    }else {
                        predicate = builder.between(getPath(root,propertyName),((Comparable)oArrBetween[0]),((Comparable)oArrBetween[1]));
                    }
                }
                break;
            case NOTBETWEEN:
                Object[] oArrNotBetween = (value.length == 1 && value[0].getClass().isArray()) ? (Object[])value[0] : value;
                if (oArrNotBetween.length == 2){
                    Class javaType = getPath(root,propertyName).getJavaType();
                    if (Date.class.equals(javaType)){
                        if (oArrNotBetween[0] instanceof String){
                            predicate = builder.between(getPath(root,propertyName),((Comparable) DateUtil.parse((String)oArrNotBetween[0])),((Comparable) DateUtil.parseDateTimeOrEndOfDay((String)oArrNotBetween[1]))).not();
                        }else if(oArrNotBetween[0] instanceof Long){
                            predicate = builder.between(getPath(root,propertyName),((Comparable)new Date(Convert.toLong(oArrNotBetween[0]))),((Comparable)new Date(Convert.toLong(oArrNotBetween[0])))).not();
                        }else /*if(oArrNotBetween[0] instanceof Date)*/{
                            predicate = builder.between(getPath(root,propertyName),((Comparable)oArrNotBetween[0]),((Comparable)oArrNotBetween[1])).not();
                        }
                    }else {
                        predicate = builder.between(getPath(root,propertyName),((Comparable)oArrNotBetween[0]),((Comparable)oArrNotBetween[1])).not();
                    }
                }
                break;
            default:
                log.error("what are you 弄啥嘞?");
                predicate = builder.conjunction();
        }
        return predicate;
    }
}
