package com.freeman.job.service;


import com.freeman.common.base.service.IBaseService;
import com.freeman.job.domain.JobLog;
import com.freeman.spring.data.request.QueryRequest;
import org.springframework.data.domain.Page;

public interface IJobLogService extends IBaseService<JobLog,Long> {
    Page<JobLog> findPage(QueryRequest queryRequest, JobLog jobLog);
}
