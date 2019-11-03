package com.freeman.job.service.impl;


import cn.hutool.core.convert.Convert;
import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.common.utils.StrUtil;
import com.freeman.job.domain.Job;
import com.freeman.job.repository.JobRepository;
import com.freeman.job.service.IJobService;
import com.freeman.job.utils.ScheduleUtil;
import com.freeman.spring.data.repository.NativeSqlQuery;
import com.freeman.spring.data.utils.request.QueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
public class JobServiceImpl extends BaseServiceImpl<JobRepository, Job,Long> implements IJobService {

    @Resource
    private Scheduler scheduler;


    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init() {
        List<Job> scheduleJobList = dao.findAll();
        // 如果不存在，则创建
        scheduleJobList.forEach(scheduleJob -> {
            CronTrigger cronTrigger = ScheduleUtil.getCronTrigger(scheduler, scheduleJob.getJobId());
            if (cronTrigger == null) {
                ScheduleUtil.createScheduleJob(scheduler, scheduleJob);
            } else {
                ScheduleUtil.updateScheduleJob(scheduler, scheduleJob);
            }
        });
    }

    /** 获取任务 */
    @Override
    public Page<Job> findPage(QueryRequest queryRequest, Job job) {

        PageRequest pageRequest = queryRequest.getPageRequest();

        String parameter = job.getParameter();
         String remark = job.getRemark();
        Object createTime = job.getParams("createTime"); //传进来是数组

        NativeSqlQuery nativeSql = NativeSqlQuery.builder()
                .eq( "bean_name", job.getBeanName())
                .eq( "method_name", job.getMethodName())
                .like( StrUtil.isNotBlank(parameter),"parameter", "%"+parameter+"%")
                .like( StrUtil.isNotBlank(parameter),"remark", "%"+remark+"%")
                .eq( "status", job.getStatus())
                .between(Objects.nonNull(createTime), "create_time", ((Object[])createTime)[0], ((Object[])createTime)[1]);

        return dao.findAllByNativeSql(nativeSql, Job.class, pageRequest);
    }


    @Override
    @Transactional
    public Job save(Job job) {
        if (job.isNew()) {
            job.setCreateTime(new Date());
            job.setStatus(Job.ScheduleStatus.PAUSE.getValue());
            ScheduleUtil.createScheduleJob(scheduler, job);
        }else {
            ScheduleUtil.updateScheduleJob(scheduler, job);
        }
        return super.save(job);
    }

    @Override
    @Transactional
    public int updateBatch(String jobIds, String status) {
        List<Long> ids = Convert.toList(Long.class, jobIds.split(StrUtil.COMMA));
        return dao.updateStatusByInIn(status, ids);
    }

    @Override
    @Transactional
    public void run(String jobIds) {
        List<Long> list = Convert.toList(Long.class, jobIds.split(StrUtil.COMMA));
        list.stream().forEach(jobId -> ScheduleUtil.run(scheduler, super.findById(jobId).get()));
    }

    @Override
    @Transactional
    public void pause(String jobIds) {
        List<Long> list = Convert.toList(Long.class, jobIds.split(StrUtil.COMMA));
        list.stream().forEach(jobId -> ScheduleUtil.pauseJob(scheduler, jobId));
        this.updateBatch(jobIds, Job.ScheduleStatus.PAUSE.getValue());
    }

    @Override
    @Transactional
    public void resume(String jobIds) {
        List<Long> list = Convert.toList(Long.class, jobIds.split(StrUtil.COMMA));
        list.stream().forEach(jobId -> ScheduleUtil.resumeJob(scheduler, jobId));
        this.updateBatch(jobIds, Job.ScheduleStatus.NORMAL.getValue());
    }
}
