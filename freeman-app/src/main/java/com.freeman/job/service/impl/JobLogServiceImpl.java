package com.freeman.job.service.impl;


import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.common.utils.StrUtil;
import com.freeman.job.domain.JobLog;
import com.freeman.job.repository.JobLogRepository;
import com.freeman.job.service.IJobLogService;
import com.freeman.spring.data.repository.specquery.QueryWrapper;
import com.freeman.spring.data.request.QueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
public class JobLogServiceImpl extends BaseServiceImpl<JobLogRepository, JobLog,Long> implements IJobLogService {

    /** 调度日志 */
    @Override
    public Page<JobLog> findPage(QueryRequest queryRequest, JobLog jobLog) {
        String beanName = jobLog.getBeanName();
        String methodName = jobLog.getMethodName();
        String parameter = jobLog.getParameter();
        String status = jobLog.getStatus();
        Map<String, Object> params = jobLog.getParams();
        Object createTime = null; //传进来是数组
        if (!ObjectUtils.isEmpty(params)) {
            createTime = params.get("createTime");
        }

        QueryWrapper<JobLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(beanName), "beanName", beanName)
            .eq(StrUtil.isNotBlank(methodName), "methodName", methodName)
            .like(StrUtil.isNotBlank(parameter), "methodName", parameter)
            .eq(StrUtil.isNotBlank(status), "status", status)
            .between(Objects.nonNull(createTime), "createTime", createTime);

        PageRequest pageRequest = queryRequest.setDefaultSortField("createTime", false).getPageRequest();

        return dao.findAll(queryWrapper, pageRequest);
    }


}
