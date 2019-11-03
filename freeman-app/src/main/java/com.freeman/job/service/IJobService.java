package com.freeman.job.service;


import com.freeman.common.base.service.IBaseService;
import com.freeman.job.domain.Job;
import com.freeman.spring.data.utils.request.QueryRequest;
import org.springframework.data.domain.Page;

public interface IJobService extends IBaseService<Job,Long> {

    Page<Job> findPage(QueryRequest queryRequest, Job job);

    int updateBatch(String jobIds, String status);

    void run(String jobIds);

    void pause(String jobIds);

    void resume(String jobIds);

}
