package com.freeman.job.service.impl;


import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.common.utils.StrUtil;
import com.freeman.job.domain.JobLog;
import com.freeman.job.repository.JobLogRepository;
import com.freeman.job.service.IJobLogService;
import com.freeman.spring.data.repository.NativeSqlQuery;
import com.freeman.spring.data.utils.request.QueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
public class JobLogServiceImpl extends BaseServiceImpl<JobLogRepository, JobLog,Long> implements IJobLogService {

    /** 调度日志 */
    @Override
    public Page<JobLog> findPage(QueryRequest queryRequest, JobLog jobLog) {

        PageRequest pageRequest = queryRequest.getPageRequest();

        Object createTime = jobLog.getParams("create_time"); //传进来是数组

        NativeSqlQuery nativeSql = NativeSqlQuery.builder()
            .from("t_job_log")
            .eq( "bean_name", jobLog.getBeanName())
            .eq( "method_name", jobLog.getMethodName())
            .contains("parameter", jobLog.getParameter())
            .eq( "status", jobLog.getStatus())
            .between( "date_format(create_time,'%Y-%m-%d')", createTime)
            .build();

        return dao.findAllByNativeSql(nativeSql, JobLog.class, pageRequest);
    }


}
