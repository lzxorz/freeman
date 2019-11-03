package com.freeman.sys.repository;


import com.freeman.sys.domain.SysLog;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysLogRepository extends BaseRepository<SysLog,Long> {
}