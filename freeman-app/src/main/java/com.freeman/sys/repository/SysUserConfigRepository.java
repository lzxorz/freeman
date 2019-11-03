package com.freeman.sys.repository;


import com.freeman.sys.domain.SysUserConfig;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserConfigRepository extends BaseRepository<SysUserConfig,Long> {

    SysUserConfig findByUserId(Long userId);
}