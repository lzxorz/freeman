package com.freeman.sys.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.freeman.spring.data.utils.request.QueryRequest;
import com.freeman.common.base.service.IBaseService;
import com.freeman.sys.domain.SysLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;


public interface ISysLogService extends IBaseService<SysLog, Long> {

    Page<SysLog> findPage(SysLog sysLog, QueryRequest queryRequest);

    @Async
    void saveLog(ProceedingJoinPoint point, SysLog log) throws JsonProcessingException;
}
