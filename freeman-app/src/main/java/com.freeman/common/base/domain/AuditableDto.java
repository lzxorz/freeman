package com.freeman.common.base.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AuditableDto implements Serializable {

    private static final long serialVersionUID = -6605381708580620587L;

    private Long createBy;
    private Date createTime;
    private Long updateBy;
    private Date updateTime;
    private String delFlag;
}
