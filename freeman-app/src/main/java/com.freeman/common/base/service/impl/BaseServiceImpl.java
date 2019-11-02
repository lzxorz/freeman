package com.freeman.common.base.service.impl;


import com.freeman.common.base.service.IBaseService;
import com.freeman.common.utils.BeanUtil;
import com.freeman.spring.data.domain.BaseEntity;
import com.freeman.spring.data.repository.BaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Transactional(readOnly = true)
public abstract class BaseServiceImpl<D extends BaseRepository, T extends BaseEntity,ID extends Serializable> implements IBaseService<T,ID> {

	@PersistenceContext
	protected EntityManager entityManager;

	/** 持久层对象 */
	@Autowired(required = false)
	protected D dao;

	/** 需要写模板查询(一个模板查询方法findAllByTemplate) */
	/*@Override
	public List<T> findAllByTemplate(T t){
		log.error("[{}]需要在Dao接口中覆盖findAllByTemplate方法，还需要写模板查询",this.getClass().getSimpleName());
		return repository.findAllByTemplate(t);
	}
	@Override
	public List<T> findAllByTemplate(T t, Sort sort){
		log.error("[{}]需要在Dao接口中覆盖findAllByTemplate方法，还需要写模板查询",this.getClass().getSimpleName());
		return repository.findAllByTemplate(t, sort);
	}

	@Override
	public Page<T> findAllByTemplate(T t, Pageable pageable){
		log.error("[{}]需要在Dao接口中覆盖findAllByTemplate方法，还需要写模板查询",this.getClass().getSimpleName());
		return repository.findAllByTemplate(t, pageable);
	}*/

	/** Specification 使用QueryWrapper 生成spec */
	/*@Override
	public Optional<T> findOne(@Nullable Specification<T> spec){
		return repository.findOne(spec);
	}
	@Override
	public List<T> findAll(@Nullable Specification<T> spec){
		return repository.findAll(spec);
	}
	@Override
	public List<T> findAll(@Nullable Specification<T> spec, Sort sort){
		return repository.findAll(spec, sort);
	}
	@Override
	public Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable){
		return repository.findAll(spec, pageable);
	}
	@Override
	public long count(@Nullable Specification<T> spec){
		return repository.count(spec);
	}*/

	/** 查询多条 */
	@Override
	public List<T> findAll(){ return dao.findAll(); }

	@Override
	public List<T> findAll(Sort sort) { return dao.findAll(sort); }

	@Override
	public Page<T> findAll(Pageable pageable) { return dao.findAll(pageable); }

	@Override
	public List<T> findAllById(Iterable<ID> ids) { return dao.findAllById(ids); }

	/** 查询,并封装到Map */
	@Override
	public Map<ID, T> mgetAll() { return dao.mgetAll(); }
	/** 根据ID查询,并封装到Map */
	@Override
	public Map<ID, T> mgetAllById(Iterable<ID> ids) { return dao.mgetAllById(ids); }


	@Override
	public Optional<T> findById(ID id){ return dao.findById(id); }


	/** 新增和修改 根据ID来判断执行 新增、修改 */
	@Override
	@Transactional
	public T save(T t){
		return (T)dao.save(t);
	}

	@Override
	@Transactional
	public List<T> saveAll(Iterable<T> iter) { return dao.saveAll(iter); }

	public void flush(){ dao.flush();}

	@Override
	@Transactional
	public T saveAndFlush(T t){ return (T)dao.saveAndFlush(t); }


	/** 物理删除 */
	@Override
	@Transactional
	public void deleteById(ID id){ dao.deleteById(id); }

	@Override
	@Transactional
	public void deleteById(Iterable<ID> ids) { dao.deleteById(ids); }

	@Override
	@Transactional
	public void delete(T t) { dao.delete(t); }

	@Override
	@Transactional
	public void deleteAll(Iterable<T> iter){ dao.deleteAll(iter); } // 多条sql

	@Override
	@Transactional
	public void deleteAll() { dao.deleteAll(); } // 多条sql

	@Override
	@Transactional
	public void deleteInBatch(Iterable<T> iter) { dao.deleteInBatch(iter); } // 一条sql

	@Override
	@Transactional
	public void deleteAllInBatch() { dao.deleteAllInBatch(); }  // 一条sql

	/** 统计数量 */
	@Override
	public long count() {
		return dao.count();
	}


	/** 判断有没有 */
	@Override
	public boolean existsById(ID id) {
		return dao.existsById(id);
	}


}
