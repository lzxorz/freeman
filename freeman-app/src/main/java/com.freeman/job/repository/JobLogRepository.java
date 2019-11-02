package com.freeman.job.repository;


import com.freeman.job.domain.JobLog;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobLogRepository extends BaseRepository<JobLog,Long> {
}