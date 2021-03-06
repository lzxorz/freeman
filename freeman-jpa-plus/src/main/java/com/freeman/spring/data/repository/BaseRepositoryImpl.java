/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.freeman.spring.data.repository;

import com.freeman.common.utils.BeanUtil;
import com.freeman.spring.data.hibernate.transformer.AliasToBeanTransformer;
import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Session;
import org.hibernate.query.internal.NativeQueryImpl;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.print.attribute.standard.MediaSize;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

/**
 * dao
 * @author 刘志新
 */
@Transactional(readOnly = true)
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private final JpaEntityInformation<T, ?> eif;
    private final EntityManager em;
    private Class<T> entityClass;
    private String entityName;
    private String idName;


    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public BaseRepositoryImpl(JpaEntityInformation<T, ID> eif, EntityManager em) {
        super(eif,em);
        this.eif = eif;
        this.em = em;

        this.entityClass = eif.getJavaType();
        this.entityName  = eif.getEntityName();
        this.idName      = eif.getIdAttributeNames().iterator().next();
    }


    @Override
    public Class<T> getEntityClass(){ return this.entityClass; }

    @Override
    public String getEntityName() { return entityName; }


    @Transactional
    public <S extends T> S save(S entity) {
        if (this.eif.isNew(entity)) {
            this.em.persist(entity);
            return entity;
        } else {
            T po = super.findById(((ID) eif.getId(entity))).orElse(null);
            if (Objects.nonNull(po)) {
                BeanUtil.copyProperties(entity, po);
                return (S) this.em.merge(po);
            }
            return this.em.merge(entity);
        }
    }



    /** 根据主键删除相应实体 */
    @Override
    @Transactional
    public void deleteById(Iterable<ID> ids) {
        if (ObjectUtils.isEmpty(ids)) return;
        List<T> models = new ArrayList();
        for (ID id : ids) {
            T model;
            try {
                model = entityClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("batch delete " + entityClass + " error", e);
            }
            try {
                BeanUtils.setProperty(model, idName, id);
            } catch (Exception e) {
                throw new RuntimeException("batch delete " + entityClass + " error, can not set id", e);
            }
            models.add(model);
        }
        deleteInBatch(models);
    }

    /** 删除实体 */
    @Override
    @Transactional
    public void delete(final T entity) {
        if (entity == null) return;
        this.em.remove(this.em.contains(entity) ? entity : this.em.merge(entity));
    }

    public Optional<T> findById(ID id) {
        if (ObjectUtils.isEmpty(id)) return null;
        return super.findById(id);
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        if (ObjectUtils.isEmpty(ids)) return null;
        return super.findAllById(ids);
    }

    /** 查询,并封装到Map */
    @Override
    public Map<ID, T> mgetAll() { return toMap(findAll()); }
    @Override
    public Map<ID, T> mgetAllById(Iterable<ID> ids) { return toMap(findAllById(ids)); }
    private Map<ID, T> toMap(List<T> list) {
        Map<ID, T> result = new LinkedHashMap<>();
        for (T t : list) {
            if (t != null) {
                result.put((ID)eif.getId(t), t);
            }
        }
        return result;
    }

    /***********************************sql*************************************************/
    // select * where id = ?1 name = ?2 ...
    public List<?> findAllBySql(String sql, Class<?> clazz, Object... params) {
        Query query = em.unwrap(Session.class).createNativeQuery(sql);
        setParameters(query, params);
        return query.unwrap(NativeQueryImpl.class).setResultTransformer(new AliasToBeanTransformer(clazz)).list();
    }

    @Override
    public List<?> findAllBySql(NativeSqlQuery nativeSql, Class<?> resultClass) {
        Query query = em.unwrap(Session.class).createNativeQuery(nativeSql.toSqlStr());
        setParameters(query, nativeSql.getParams());
        return query.unwrap(NativeQueryImpl.class).setResultTransformer(new AliasToBeanTransformer(resultClass)).list();
    }

    @Override
    public Page findAllBySql(NativeSqlQuery nativeSql, Class<?> resultClass, Integer pageNo, Integer pageSize) {
        String sqlStr = nativeSql.toSqlStr();

        //获取总记录数
        Session session = em.unwrap(Session.class);
        Query countQuery = session.createNativeQuery("select count(*) from (" + sqlStr + ") as t");
        setParameters(countQuery, nativeSql.getParams());


        //获取分页结果
        Query pageQuery = session.createNativeQuery(sqlStr);
        setParameters(pageQuery, nativeSql.getParams());

        long totalRecord = ((Number) countQuery.getSingleResult()).longValue();
        List result = totalRecord == 0 ? new ArrayList<>(0) :
            pageQuery.setFirstResult(pageNo-1)
                .setMaxResults(pageSize)
                .unwrap(NativeQueryImpl.class)
                .setResultTransformer(new AliasToBeanTransformer(resultClass))
                // .setResultTransformer(Transformers.aliasToBean(resultClass))
                // .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
        
        return new PageImpl<>(result,PageRequest.of(pageNo-1, pageSize),totalRecord);
    }


    /***********************************ql*************************************************/

    /** 根据ql和按照索引顺序的params查询一个实体 */
    public T findOneByQL(final String ql, final Object... params) {
        List<T> list = findAllByQL(ql, PageRequest.of(0, 1), params);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public List<T> findAllByQL(final String ql, final Object... params) {
        return findAllByQL(ql, (Pageable) null, params);
    }

    /**
     * 根据ql和按照索引顺序的params执行ql，sort存储排序信息 null表示不排序
     *
     * @param ql
     * @param sort   null表示不排序
     * @param params
     *      List<Order> orders = this.find("SELECT o FROM Order o WHERE o.storeId = ? and o.code = ? order by o.createTime desc", storeId, code);
     * @return
     */
    public List<T> findAllByQL(final String ql, final Sort sort, final Object... params) {
        Query query = em.createQuery(ql + prepareOrder(sort));
        setParameters(query, params);
        return query.getResultList();
    }

    /**
     * 根据ql和按照索引顺序的params执行ql，pageable存储分页信息 null表示不分页
     *
     * @param ql
     * @param pageable null表示不分页
     * @param params
     * @return
     */
    public List<T> findAllByQL(final String ql, final Pageable pageable, final Object... params) {
        Query query = em.createQuery(ql + prepareOrder(pageable != null ? pageable.getSort() : null));
        setParameters(query, params);
        if (pageable != null) {
            query.setFirstResult((int)pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        return query.getResultList();
    }

    /** 根据ql和按照索引顺序的params执行ql统计 */
    public long countByQL(final String ql, final Object... params) {
        Query query = em.createQuery(ql);
        setParameters(query, params);
        return (Long) query.getSingleResult();
    }

    /************************************************************************************/

    /** 拼排序 */
    private String prepareOrder(Sort sort) {
        return (sort == null || !sort.iterator().hasNext()) ? "" :
                (" order by " + sort.toString().replace(":", " "));
    }

    /** 按列名设置Query参数 */
    // private void setParameters(Query query, Map<String, Object> params) {
    //     if (params != null) {
    //         for (String key : params.keySet()) {
    //             query.setParameter(key, params.get(key));
    //         }
    //     }
    // }

    /** 按顺序设置Query参数 */
    private void setParameters(Query query, Object[] params) {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
    }

    /** 按顺序设置Query参数 */
    private void setParameters(Query query, List params) {
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i + 1, params.get(i));
            }
        }
    }
}
