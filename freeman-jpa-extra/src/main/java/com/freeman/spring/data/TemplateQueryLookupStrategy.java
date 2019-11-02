package com.freeman.spring.data;


import com.freeman.spring.data.annotation.TemplateQuery;
import com.freeman.spring.data.sqltemplate.EnjoyTemplateQuery;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;

/**
 * 模板查询策略
 */
public class TemplateQueryLookupStrategy implements QueryLookupStrategy {

    private final EntityManager entityManager;

    private QueryLookupStrategy jpaQueryLookupStrategy;

    private QueryExtractor extractor;

    public TemplateQueryLookupStrategy(EntityManager entityManager, Key key, QueryExtractor extractor,
                                       QueryMethodEvaluationContextProvider evaluationContextProvider, EscapeCharacter escape) {
        this.jpaQueryLookupStrategy = JpaQueryLookupStrategy.create(entityManager, key, extractor, evaluationContextProvider,escape);
        this.extractor = extractor;
        this.entityManager = entityManager;
    }

    /** BaseRepositoryFactory-->动态创建查询策略 */
    public static QueryLookupStrategy create(EntityManager entityManager, Key key, QueryExtractor extractor,
                                             QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return new TemplateQueryLookupStrategy(entityManager, key, extractor, evaluationContextProvider, EscapeCharacter.of('\\'));
    }

    /** 解析为 模板引擎查询 */
    @Override
    public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
        // 判断方法上是否有注解TemplateQuery,如果有则使用动态sql
        if (method.getAnnotation(TemplateQuery.class) != null) {
            return new EnjoyTemplateQuery(new TplJpaQueryMethod(method, metadata, factory, extractor), entityManager);
        } else { //没有则使用jpa默认
            return jpaQueryLookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
        }
    }


}
