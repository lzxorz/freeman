package com.freeman.common.base.service;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface IBaseService<T,ID extends Serializable> {
    //======= 自定义 模板查询 ====== //
    /** 需要写模板查询(一个模板查询方法findAllByTemplate) */
     // List<T> findAllByTemplate(T t); // 需要写模板查询
     // List<T> findAllByTemplate(T t, Sort sort); // 需要写模板查询
     // Page<T> findAllByTemplate(T t, Pageable pageable); // 需要写模板查询

     /** Specification 使用QueryWrapper 生成spec */
     // Optional<T> findOne(@Nullable T entity);
     // List<T> findAll(@Nullable T entity);
     // List<T> findAll(@Nullable T entity, Sort sort);
     // Page<T> findAll(@Nullable T entity, Pageable pageable);
     // long count(@Nullable T entity);

	 /** 查询多条 */
     List<T> findAll();
     List<T> findAll(Sort sort);
     Page<T> findAll(Pageable pageable);
     List<T> findAllById(Iterable<ID> ids);
     Map<ID, T> mgetAll(); // 查询,并封装到Map
     Map<ID, T> mgetAllById(Iterable<ID> ids); // 根据ID查询,并封装到Map


     /** 查询单条 */
     //T getOne(ID userId);
     Optional<T> findById(ID id);


     /** 新增和修改 根据ID来判断执行 新增、修改 */
     T save(T t);
     List<T> saveAll(Iterable<T> list);
     void flush();
     T saveAndFlush(T t);


     /** 物理删除 */
     void deleteById(ID id);
     void deleteById(Iterable<ID> ids);
     void delete(T t);
     void deleteAll(Iterable<T> ite); // 多条sql
     void deleteAll();                          // 多条sql
     void deleteInBatch(Iterable<T> ite);       // 一条sql
     void deleteAllInBatch();                   // 一条sql


     /** 统计数量 */
     long count();

     /** 判断是否存在 */
     boolean existsById(ID id);

}
