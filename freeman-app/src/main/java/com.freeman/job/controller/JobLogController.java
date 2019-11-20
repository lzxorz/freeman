package com.freeman.job.controller;


import cn.hutool.core.convert.Convert;
import com.freeman.common.result.R;
import com.freeman.common.base.controller.BaseController;
import com.freeman.job.domain.JobLog;
import com.freeman.job.service.IJobLogService;
import com.freeman.spring.data.utils.request.QueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("job/log")
public class JobLogController extends BaseController {

    @Autowired
    private IJobLogService jobLogService;

    @GetMapping
    @RequiresPermissions("jobLog:view")
    public R jobLogList(QueryRequest request, JobLog log) {
        return R.ok(this.jobLogService.findPage(log,request));
    }

    // 删除调度日志
    @DeleteMapping("/{jobIds}")
    @RequiresPermissions("jobLog:delete")
    public void deleteJobLog(@NotBlank(message = "{required}") @PathVariable String jobIds) {
        List<Long> ids = Convert.toList(Long.class, jobIds);
        jobLogService.deleteById(ids);

    }

    /*@PostMapping("excel")
    @RequiresPermissions("jobLog:export")
    public void export(QueryRequest request, JobLog jobLog, HttpServletResponse response) {
        List<JobLog> jobLogs = this.jobLogService.findJobLogs(request, jobLog).getRecords();
        ExcelKit.$Export(JobLog.class, response).downXlsx(jobLogs, false);

    }*/
}
