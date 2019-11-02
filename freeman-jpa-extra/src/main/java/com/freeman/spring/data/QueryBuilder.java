package com.freeman.spring.data;

import com.freeman.spring.data.annotation.TemplateQuery;
import com.freeman.spring.data.hibernate.transformer.SingleBeanTransformerAdapter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.query.QueryParameter;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class QueryBuilder {

    private static final Pattern ORDERBY_PATTERN_1 = Pattern.compile("order\\s+by.+?$", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    private static Map<Class, ResultTransformer> transformerCache = new ConcurrentHashMap<>();

    public static <C> Query transform(Query query, Class<C> clazz) {
        ResultTransformer transformer;
        if (Map.class.isAssignableFrom(clazz)) {
            transformer = Transformers.ALIAS_TO_ENTITY_MAP;
        } else if (Number.class.isAssignableFrom(clazz) || clazz.isPrimitive() || String.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)) {
            transformer = transformerCache.computeIfAbsent(clazz, SmartTransformer::new);
        } else {
            transformer = transformerCache.computeIfAbsent(clazz, SingleBeanTransformerAdapter::new);
        }
        return query.setResultTransformer(transformer);
    }

    public static NativeQuery toNativeQuery(EntityManager em, String nativeQuery, Object beanOrMap) {
        Session session = em.unwrap(Session.class);
        NativeQuery query = session.createNativeQuery(nativeQuery);
        setParams(query, beanOrMap);
        return query;
    }

    public static <C> Query transform(Query query, Class<C> clazz, TemplateQuery templateQuery) {
        if (Map.class.isAssignableFrom(clazz)) {
            return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        } else if (Number.class.isAssignableFrom(clazz) || clazz.isPrimitive() || String.class.isAssignableFrom(clazz) ||
                Date.class.isAssignableFrom(clazz)) {
            return query.setResultTransformer(new SmartTransformer(clazz));
        }

        try {
            Class<?> aClass = templateQuery.resultTransformer();
            Constructor c = aClass.getConstructor(Class.class);
            return query.setResultTransformer((ResultTransformer) c.newInstance(clazz));
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        // return query.setResultTransformer(new BeanTransformerAdapter<C>(clazz));
        // return query.setResultTransformer(new AliasToBeanTransformerAdapter<>(clazz));
    }

    /** 包装 统计查询语句 */
    private static String wrapCountQuery(String query) {
        return "select count(1) from (" + query + ") as ctmp";
    }

    /** 清掉order by */
    private static String cleanOrderBy(String query) {
        Matcher matcher = ORDERBY_PATTERN_1.matcher(query);
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while (matcher.find()) {
            String part = matcher.group(i);
            if (canClean(part)) {
                matcher.appendReplacement(sb, "");
            } else {
                matcher.appendReplacement(sb, part);
            }
            i++;
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static boolean canClean(String orderByPart) {
        return orderByPart != null && (!orderByPart.contains(")")
                || StringUtils.countOccurrencesOf(orderByPart, ")") == StringUtils.countOccurrencesOf(orderByPart, "("));
    }

    /** 根据select sql 构造出 count sql 统计总数量 */
    public static String toCountQuery(String query) {
        return wrapCountQuery(cleanOrderBy(query));
    }

    /** NativeQuery#set 传参 */
    public static void setParams(Query query, Object beanOrMap) {
        Collection<QueryParameter> nps = query.getParameterMetadata().getNamedParameters();
        if (nps != null) {
            Map<String, Object> params = toParams(beanOrMap);
            for (QueryParameter qp : nps) {
                String qpName = qp.getName();
                Object arg = params.get(qpName);
                if (arg == null) {
                    query.setParameter(qp, null);
                } else if (arg.getClass().isArray()) {
                    query.setParameterList(qpName, (Object[]) arg);
                } else if (arg instanceof Collection) {
                    query.setParameterList(qp, ((Collection) arg));
                } else if (arg.getClass().isEnum()) {
                    query.setParameter(qp, ((Enum) arg).ordinal());
                } else {
                    query.setParameter(qp, arg);
                }
            }
        }
    }

    /** 传参实体类,所有属性和传入的值封装到Map --> 删除无意义值所在的 key:val */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toParams(Object beanOrMap) {
        Map<String, Object> params;
        if (beanOrMap instanceof Map) {
            params = (Map<String, Object>) beanOrMap;
        } else {
            params = toMap(beanOrMap);
        }
        if (!CollectionUtils.isEmpty(params)) {
            Iterator<String> keys = params.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                if (!isValidValue(params.get(key))) {
                    keys.remove();
                }
            }
        }
        return params;
    }

    /** 判断是有效的值(非null,非空集合) */
    public static boolean isValidValue(Object object) {
        if (object == null) {
            return false;
        }
        /*if (object instanceof Number && ((Number) object).longValue() == 0) {
            return false;
		}*/
        return !(object instanceof Collection && CollectionUtils.isEmpty((Collection<?>) object));
    }

    /** 传参实体类,所有属性和传入的值封装到Map */
    public static Map<String, Object> toMap(Object bean) {
        if (bean == null) {
            return Collections.emptyMap();
        }
        try {
            Map<String, Object> description = new HashMap<String, Object>();
            if (bean instanceof DynaBean) {
                DynaProperty[] descriptors = ((DynaBean) bean).getDynaClass().getDynaProperties();
                for (DynaProperty descriptor : descriptors) {
                    String name = descriptor.getName();
                    description.put(name, BeanUtils.getProperty(bean, name));
                }
            } else {
                PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(bean);
                for (PropertyDescriptor descriptor : descriptors) {
                    String name = descriptor.getName();
                    if (PropertyUtils.getReadMethod(descriptor) != null) {
                        description.put(name, PropertyUtils.getNestedProperty(bean, name));
                    }
                }
            }
            return description;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

}
