package com.freeman.spring.data.repository;

import com.freeman.spring.data.TemplateQueryLookupStrategy;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Optional;


public class BaseRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {

    private final EntityManager entityManager;
    private final PersistenceProvider extractor;



    /**
     * Creates a new {@link JpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public BaseRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        this.extractor = PersistenceProvider.fromEntityManager(entityManager);
   }

    /** 动态创建查询策略 */
    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return Optional.of(TemplateQueryLookupStrategy.create(entityManager, key, extractor, evaluationContextProvider));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        Class<?> repositoryInterface = information.getRepositoryInterface();

        if (isBaseRepository(repositoryInterface)) {
            JpaEntityInformation<T, ID> entityInformation = getEntityInformation((Class<T>) information.getDomainType());
            BaseRepositoryImpl<T, ID> repository = new BaseRepositoryImpl(entityInformation, entityManager);

            return repository;
        }
        return super.getTargetRepository(information, entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (isBaseRepository(metadata.getRepositoryInterface())) {
            return BaseRepositoryImpl.class;
        }
        return super.getRepositoryBaseClass(metadata);
    }

    private boolean isBaseRepository(Class<?> repositoryInterface) {
        return BaseRepository.class.isAssignableFrom(repositoryInterface);
    }

}
