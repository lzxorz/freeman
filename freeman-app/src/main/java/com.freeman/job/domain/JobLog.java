package com.freeman.job.domain;


import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.BaseEntity;
import com.freeman.spring.data.domain.idgenerate.IdGen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Entity(name = "t_job_log") @TableAlias("tjl")
public class JobLog extends BaseEntity<Job,Long> {

    private static final long serialVersionUID = -7114915445674333148L;
    // 任务执行成功
    public static final String JOB_SUCCESS = "0";
    // 任务执行失败
    public static final String JOB_FAIL = "1";

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long logId;

    private Long jobId;

    //@ExcelField(value = "Bean名称")
    private String beanName;

    //@ExcelField(value = "方法名称")
    private String methodName;

    //@ExcelField(value = "方法参数")
    private String parameter;

    //@ExcelField(value = "状态", writeConverterExp = "0=成功,1=失败")
    private String status;

    //@ExcelField(value = "异常信息")
    private String error;

    //@ExcelField(value = "耗时（毫秒）")
    private Long times;

    //@ExcelField(value = "执行时间", converter = TimeConverter.class)
    private Date createTime;

    public Long getId() {
        return jobId;
    }

}
