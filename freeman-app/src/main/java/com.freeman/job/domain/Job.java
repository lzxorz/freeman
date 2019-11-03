package com.freeman.job.domain;


import com.freeman.common.cron.IsCron;
import com.freeman.common.utils.excel.annotation.ExcelField;
import com.freeman.common.utils.excel.converter.TimeConverter;
import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.BaseEntity;
import com.freeman.spring.data.utils.idgenerate.IdGen;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;


@Data
@Entity(name = "t_job") @TableAlias("tj")
public class Job extends BaseEntity<Job,Long> {

    private static final long serialVersionUID = 400066840871805700L;

    /**
     * 任务调度参数 key
     */
    public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";

    public enum ScheduleStatus {
        /** 正常 */
        NORMAL("0"),
        /** 暂停 */
        PAUSE("1");

        private String value;

        ScheduleStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long jobId;

    @NotBlank(message = "{required}")
    @Size(max = 50, message = "{noMoreThan}")
    @ExcelField(title = "Bean名称")
    private String beanName;

    @NotBlank(message = "{required}")
    @Size(max = 50, message = "{noMoreThan}")
    @ExcelField(title = "方法名称")
    private String methodName;

    @Size(max = 50, message = "{noMoreThan}")
    @ExcelField(title = "方法参数")
    private String parameter;

    @NotBlank(message = "{required}")
    @IsCron(message = "{invalid}")
    @ExcelField(title = "Cron表达式")
    private String cronExpression;

    @ExcelField(title = "状态") // "0=正常,1=暂停"
    private String status;

    @Size(max = 100, message = "{noMoreThan}")
    @ExcelField(title = "备注")
    private String remark;

    @ExcelField(title = "创建时间", converter = TimeConverter.class)
    private Date createTime;


    public Long getId() {
        return jobId;
    }
}
