package com.freeman.spring.data.domain;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


//@JsonIgnoreProperties(value = {"createdDate","createdBy", "lastModifiedDate", "lastModifiedBy"},allowGetters = true) //jackson只能获取createdDate,createdBy,lastModifiedDate,lastModifiedBy属性(其他创建和更新操作都由jpa完成)
@EntityListeners({AuditingEntityListener.class})//用于监听实体类操作的。
@MappedSuperclass //类将不是一个完整的实体类，他将不会映射到数据库表，但是他的属性都将映射到其子类的数据库字段中。 //http://blog.sina.com.cn/s/blog_7085382f0100uk4p.html
public abstract class AuditableEntity<T extends Model, ID extends Serializable> extends BaseEntity<T, ID> {

    //表示该字段为创建时间字段，在这个实体被insert的时候,会设置值. 类上加@EntityListeners(AuditingEntityListener.class),启动类加@EnableJpaAuditing. @LastModifiedDate同理.
    @Column(name = "create_time",nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    protected Date createTime;

    //表示该字段为创建人，在这个实体被insert的时候,会设置值.类上加@EntityListeners(AuditingEntityListener.class),启动类加@EnableJpaAuditing. 有实现AuditorAware<Long>接口的配置类. @LastModifiedBy同理
    @Column(name = "create_by",nullable = false, updatable = false)
    @CreatedBy
    protected Long createBy;

    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    protected Date updateTime;

    @Column(name = "update_by",nullable = false)
    @LastModifiedBy
    protected Long updateBy;

    public AuditableEntity() {}
    public AuditableEntity(Date createDate, Long createdBy, Date lastModifiedDate, Long lastModifiedBy) {
        super();
    }



    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }
}
