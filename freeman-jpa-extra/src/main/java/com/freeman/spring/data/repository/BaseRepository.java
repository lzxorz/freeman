package com.freeman.spring.data.repository;

import com.freeman.spring.data.repository.nativequery.NativeQueryWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>抽象DAO层基类 提供一些简便方法<br/>
 * 泛型 ： T 表示实体类型；ID表示主键类型
 * <p/>
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID>, JpaSpecificationExecutor<T> {
    //======= 自定义 模板查询 ====== //
    /** 需要写模板查询(一个模板查询方法findAllByTemplate) */
    //List<T> findAllByTemplate(T t);
    //List<T> findAllByTemplate(T t, Sort sort);
    //Page<T> findAllByTemplate(T t, Pageable pageable);

    //======= Crud begin ====== //
    <S extends T> S save(S s);
    <S extends T> List<S> saveAll(Iterable<S> iter);
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    List<T> findAll();
    List<T> findAllById(Iterable<ID> ids);
    long count();
    void deleteById(ID id);
    void deleteById(Iterable<ID> ids); // 根据ID删除
    void delete(T t);
    void deleteAll(Iterable<? extends T> iter);
    void deleteAll();


    //======= PageAndSorting begin ====== //
    List<T> findAll(Sort var1);
    Page<T> findAll(Pageable page);


    //======= JpaRepository begin ====== //
    void flush();
    <S extends T> S saveAndFlush(S s);
    void deleteInBatch(Iterable<T> iter);
    void deleteAllInBatch();
    //@Deprecated
    //T getOne(ID id);

    //======= 自定义 begin ====== //
    Map<ID, T> mgetAll(); // 查询,并封装到Map
    Map<ID, T> mgetAllById(Iterable<ID> ids); // 根据ID查询,并封装到Map


    // ===============动态原生sql================= //
    <T> List<T> findByNativeSql(NativeQueryWrapper sql, Class<?> resultClass);
    <T> Page<T> findByNativeSql(NativeQueryWrapper sql, Class<?> resultClass, Pageable page);

    // ================================ //

    Class<T> getEntityClass();
    String getEntityName();

}
