package com.freeman.sys.repository;


import com.freeman.sys.domain.SysUserConfig;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserConfigRepository extends BaseRepository<SysUserConfig,Long> {

    /** 需要写模板查询 */
    /*@TemplateQuery
    Page<SysUserConfig> findAllByTemplate(SysUserConfig sysUserConfig, Pageable pageable);*/

    SysUserConfig findByUserId(Long userId);
}