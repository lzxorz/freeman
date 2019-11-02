package com.freeman.spring.data.sqltemplate;

import cn.hutool.core.util.StrUtil;
import com.freeman.spring.data.QueryBuilder;
import com.freeman.spring.data.TplJpaQueryMethod;
import com.freeman.spring.data.annotation.TemplateQuery;
import com.freeman.utils.AopTargetUtils;
import com.freeman.utils.ApplicationContextHolder;
import com.freeman.utils.StringKit;
import org.hibernate.query.NativeQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.JpaParameters;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板查询策略解析-->模板查询
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 2015/8/9.
 */
public class EnjoyTemplateQuery extends AbstractJpaQuery {

    private boolean useJpaSpec = true;
    //private static final String physicalStrategy;
    //private static final String jpa_physicalNameStrategy = "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy"; //SpringPhysicalNamingStrategy.class.getName();
    private static final Pattern ORDERBY_PATTERN;
    private static final Pattern ORDERBY_COLUMN_PATTERN;

    static {
        //physicalStrategy = ApplicationContextHolder.getBean(HibernateProperties.class).getNaming().getPhysicalStrategy();
        ORDERBY_PATTERN = Pattern.compile("order\\s+by.+?$", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        ORDERBY_COLUMN_PATTERN = Pattern.compile("\\b[a-z][a-z\\d]*[A-Z][a-z\\d]*\\b");
    }
    /**
     * Creates a new {@link AbstractJpaQuery} from the given {@link JpaQueryMethod}.
     *
     * @param method data query method
     * @param em     domain component
     */
    public EnjoyTemplateQuery(JpaQueryMethod method, EntityManager em) {
        super(method, em);
    }

    /** 解析模板，动态构建JPA Query对象 */
    @Override
    protected Query doCreateQuery(Object[] values) {
        String nativeQuery = getQuery(values);
        JpaParameters parameters = getQueryMethod().getParameters();
        ParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);
        String sortedQueryString = QueryUtils.applySorting(nativeQuery, accessor.getSort(), QueryUtils.detectAlias(nativeQuery)); //StringQuery
        //if (jpa_physicalNameStrategy.equals(physicalStrategy)){
        sortedQueryString = buildOrderBy(sortedQueryString);
        //}
        Query query = bind(createJpaQuery(sortedQueryString), values);
        if (parameters.hasPageableParameter()) {
            Pageable pageable = (Pageable) (values[parameters.getPageableIndex()]);
            if (pageable != null) {
                query.setFirstResult((int) pageable.getOffset());
                query.setMaxResults(pageable.getPageSize());
            }
        }
        return query;
    }

    private String getQuery(Object[] values) {
        return getQueryFromTpl(values);
    }
    private String getQueryFromTpl(Object[] values) {
        return ApplicationContextHolder.getBean(EnjoySqlTemplates.class).process(getEntityName(), getMethodName(), getParams(values));
    }

    /** 获取实体类名称 */
    private String getEntityName() {
        return getQueryMethod().getEntityInformation().getJavaType().getSimpleName();
    }

    /** 获取查询方法名,优先使用@TemplateQuery()里面的value值, 没有值、用方法名 */
    private String getMethodName() {
        TplJpaQueryMethod queryMethod = (TplJpaQueryMethod) getQueryMethod();
        TemplateQuery annotation = queryMethod.getMethod().getAnnotation(TemplateQuery.class);
        if (!StrUtil.isEmpty(annotation.value())) {
            return annotation.value();
        }
        return queryMethod.getName();
    }

    /** 传参封装到Map */
    private Map<String, Object> getParams(Object[] values) {
        JpaParameters parameters = getQueryMethod().getParameters();
        //gen model
        Map<String, Object> params = new HashMap<String, Object>();
        for (int i = 0; i < parameters.getNumberOfParameters(); i++) {
            Object value = values[i];
            Parameter parameter = parameters.getParameter(i);
            if (value != null && canBindParameter(parameter)) {
                if (!QueryBuilder.isValidValue(value)) {
                    continue;
                }
                Class<?> clz = value.getClass();
                if (clz.isPrimitive() || String.class.isAssignableFrom(clz) || Number.class.isAssignableFrom(clz)
                        || clz.isArray() || Collection.class.isAssignableFrom(clz) || clz.isEnum()) {
                    params.put(parameter.getName().orElse(null), value);
                } else {
                    params = QueryBuilder.toParams(value);
                }
            }
        }
        return params;
    }

    /** 构建JPA Query对象 */
    private Query createJpaQuery(String queryString) {
        Class<?> objectType = getQueryMethod().getReturnedObjectType(); //返回的实体类class
        //get original proxy query.
        Query oriProxyQuery;
        //must be hibernate QueryImpl
        NativeQuery query;

        if (useJpaSpec && getQueryMethod().isQueryForEntity()) {
            oriProxyQuery = getEntityManager().createNativeQuery(queryString, objectType);
        } else {
            oriProxyQuery = getEntityManager().createNativeQuery(queryString);
            query = AopTargetUtils.getTarget(oriProxyQuery);

            ClassTypeInformation<?> ctif = ClassTypeInformation.from(objectType);
            TypeInformation<?> actualType = ctif.getActualType();
            Class<?> genericType = actualType == null ? objectType : actualType.getType();

            if (genericType != null && genericType != Void.class) {
                TplJpaQueryMethod queryMethod = (TplJpaQueryMethod) getQueryMethod();
                TemplateQuery annotation = queryMethod.getMethod().getAnnotation(TemplateQuery.class);
                QueryBuilder.transform(query, genericType, annotation);
                //QueryBuilder.transform(query, genericType);
            }
        }
        //return the original proxy query, for a series of JPA actions, e.g.:close em.
        return oriProxyQuery;
    }



    /** 创建统计sql */
    @Override
    @SuppressWarnings("unchecked")
    protected Query doCreateCountQuery(Object[] values) {
        //TypedQuery query = (TypedQuery) getEntityManager().createNativeQuery(QueryBuilder.toCountQuery(getNativeQueryFromTpl(values)));
        Query query = getEntityManager().createNativeQuery(QueryBuilder.toCountQuery(getQuery(values)));
        bind(query, values);
        return query;
    }

    /** Query对象 绑定sql参数 */
    private Query bind(Query query, Object[] values) {
        //get proxy target if exist.
        //must be hibernate QueryImpl
        NativeQuery targetQuery = AopTargetUtils.getTarget(query);
        Map<String, Object> params = getParams(values);
        if (!CollectionUtils.isEmpty(params)) {
            QueryBuilder.setParams(targetQuery, params); // ParameterMetadataImpl
        }
        return query;
    }

    private boolean canBindParameter(Parameter parameter) {
        return parameter.isBindable();
    }

    /**
     * order by字段 驼峰转下划线转换
     * @author 刘志新
     */
    private static String buildOrderBy(String query) {
        Matcher matcher = ORDERBY_PATTERN.matcher(query);

        StringBuffer selectSql = new StringBuffer();
        StringBuffer orderByClause = new StringBuffer();
        if (!matcher.find()) {
            return query;
        }

        String part = matcher.group(0);
        orderByClause.append(part);
        if ((!part.contains(")") || StringUtils.countOccurrencesOf(part, ")") == StringUtils.countOccurrencesOf(part, "("))) {
            matcher.appendReplacement(selectSql, "");
        }
        matcher.appendTail(selectSql);

        Matcher m = ORDERBY_COLUMN_PATTERN.matcher(orderByClause.toString());
        orderByClause.setLength(0);
        while (m.find()) {
            m.appendReplacement(orderByClause, StringKit.camelToUnderline(m.group()));
        }
        m.appendTail(orderByClause);

        return selectSql.append(orderByClause).toString();
    }
}
