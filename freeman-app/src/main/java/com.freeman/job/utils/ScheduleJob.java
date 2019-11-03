package com.freeman.job.utils;


import com.freeman.common.utils.SpringContextUtil;
import com.freeman.common.utils.StrUtil;
import com.freeman.job.domain.Job;
import com.freeman.job.domain.JobLog;
import com.freeman.job.service.IJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 定时任务
 */
@Slf4j
public class ScheduleJob extends QuartzJobBean {

    private ExecutorService service = Executors.newSingleThreadExecutor();

    @Override
    protected void executeInternal(JobExecutionContext context) {
        Job scheduleJob = (Job) context.getMergedJobDataMap().get(Job.JOB_PARAM_KEY);

        // 获取spring bean
        IJobLogService scheduleJobLogService = SpringContextUtil.getBean(IJobLogService.class);

        JobLog jobLog = JobLog.builder().jobId(scheduleJob.getJobId())
        .beanName(scheduleJob.getBeanName()).methodName(scheduleJob.getMethodName()).parameter(scheduleJob.getParameter())
        .createTime(new Date()).build();

        long startTime = System.currentTimeMillis();

        try {
            // 执行任务
            log.info("任务准备执行，任务ID：{}", scheduleJob.getJobId());
            ScheduleRunnable task = new ScheduleRunnable(scheduleJob.getBeanName(), scheduleJob.getMethodName(), scheduleJob.getParameter());
            Future<?> future = service.submit(task);
            future.get();
            long times = System.currentTimeMillis() - startTime;
            jobLog.setTimes(times);
            // 任务状态 0：成功 1：失败
            jobLog.setStatus(JobLog.JOB_SUCCESS);

            log.info("任务执行完毕，任务ID：{} 总共耗时：{} 毫秒", scheduleJob.getJobId(), times);
        } catch (Exception e) {
            log.error("任务执行失败，任务ID：" + scheduleJob.getJobId(), e);
            long times = System.currentTimeMillis() - startTime;
            jobLog.setTimes(times);
            // 任务状态 0：成功 1：失败
            jobLog.setStatus(JobLog.JOB_FAIL);
            jobLog.setError(StrUtil.sub(e.toString(), 0, 2000));
        } finally {
            scheduleJobLogService.save(jobLog);
        }
    }
}
