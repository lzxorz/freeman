package com.freeman.job.controller;


import cn.hutool.core.convert.Convert;
import com.freeman.common.log.Log;
import com.freeman.common.result.R;
import com.freeman.common.base.controller.BaseController;
import com.freeman.job.domain.Job;
import com.freeman.job.service.IJobService;
import com.freeman.spring.data.request.QueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("job")
public class JobController extends BaseController {

    @Autowired
    private IJobService jobService;

    @GetMapping
    @RequiresPermissions("job:view")
    public R jobList(QueryRequest request, Job job) {
        return R.ok(jobService.findPage(request, job));
    }

    @GetMapping("cron/check")
    public boolean checkCron(String cron) {
        try {
            return CronExpression.isValidExpression(cron);
        } catch (Exception e) {
            return false;
        }
    }

    @Log("新增定时任务")
    @PostMapping
    @RequiresPermissions("job:add")
    public void addJob(@Valid Job job) {
        jobService.save(job);
    }


    @Log("修改定时任务")
    @PutMapping
    @RequiresPermissions("job:update")
    public void updateJob(@Valid Job job){
        this.jobService.save(job);
    }

    @Log("删除定时任务")
    @DeleteMapping("/{jobIds}")
    @RequiresPermissions("job:delete")
    public void deleteJob(@NotBlank(message = "{required}") @PathVariable String jobIds){
        List<Long> ids = Convert.toList(Long.class, jobIds);
        this.jobService.deleteById(ids);
    }

    @Log("执行定时任务")
    @GetMapping("run/{jobId}")
    @RequiresPermissions("job:run")
    public void runJob(@NotBlank(message = "{required}") @PathVariable String jobId){
        this.jobService.run(jobId);
    }

    @Log("暂停定时任务")
    @GetMapping("pause/{jobId}")
    @RequiresPermissions("job:pause")
    public void pauseJob(@NotBlank(message = "{required}") @PathVariable String jobId){
        this.jobService.pause(jobId);
    }

    @Log("恢复定时任务")
    @GetMapping("resume/{jobId}")
    @RequiresPermissions("job:resume")
    public void resumeJob(@NotBlank(message = "{required}") @PathVariable String jobId){
        this.jobService.resume(jobId);
    }

    /*@PostMapping("excel")
    @RequiresPermissions("job:export")
    public void export(QueryRequest request, Job job, HttpServletResponse response){
        List<Job> jobs = this.jobService.findPage(request, job).getContent();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        ExcelExport.beanExportExcel(response,jobs).downXlsx(jobs, false);
    }*/
}
