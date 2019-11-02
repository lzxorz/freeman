package com.freeman.spring.data.domain;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

// 对实体属性变化的跟踪，它提供了保存前，保存后，更新前，更新后，删除前，删除后等状态，就像是拦截器一样，你可以在拦截方法里重写你的个性化逻辑。
// @Prepersist注解的方法 ，完成save之前的操作。
// @Preupdate注解的方法 ，完成update之前的操作。
// @PreRemove注解的方法 ，完成remove之前的操作。
// @Postpersist注解的方法 ，完成save之后的操作。
// @Postupdate注解的方法 ，完成update之后的操作。
// @PostRemovet注解的方法 ，完成remove之后的操作。
// 在需要监听的jpa实体类 @EntityListeners(BaseEntityListener.class)
// 参考链接 https://www.jianshu.com/p/14cb69646195 或 https://www.cnblogs.com/520playboy/p/7552141.htmls

@Slf4j
@Configurable
//@CreatedDate @LastModifiedDate 由AuditingEntityListener完成
//@CreatedBy LastModifiedBy 由CurrentUserAuditor完成
public class BaseEntityListener {

    /*@PrePersist
    public void touchForCreate(BaseEntity domain){
        domain.setDelFlag(BaseEntity.DEL_FLAG_NORMAL);
        //log.info("开始保存------>"+domain.toString());
    }


    @PreUpdate
    public void touchForUpdate(BaseEntity domain){
        if (null == domain.getDelFlag()){
            domain.setDelFlag(BaseEntity.DEL_FLAG_NORMAL);
        }
        //log.info("开始更新------>"+domain.toString());
    }

    @PostPersist
    public void PostPersist(Object domain){
        log.info("保存完毕------>"+domain.toString());
    }

    @PostUpdate
    public void PostUpdate(Object domain){
       log.info("更新完毕------>"+domain.toString());
    }*/

}
