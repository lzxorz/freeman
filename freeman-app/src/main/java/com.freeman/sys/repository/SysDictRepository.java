package com.freeman.sys.repository;


import com.freeman.sys.domain.SysDict;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysDictRepository extends BaseRepository<SysDict,Long> {
    /** 需要写模板查询 */
    /*@TemplateQuery
    Page<SysDict> findAllByTemplate(SysDict sysDict, Pageable pageable);*/
}