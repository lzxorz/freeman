package com.freeman.spring.data.domain;

import cn.hutool.core.util.StrUtil;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Persistable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 具备增删查功能的实体父类
 * @author 刘志新
 */
public abstract class Model<T, ID extends Serializable> implements Serializable, Persistable<ID> {
    private static final long serialVersionUID = 6882139672754605640L; //T为实体自身类型，ID为实体主键类型

    /** 用于获取容器中bean对象的上下文，由外部用Model.setApplicationContext方法传入 */
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        Model.applicationContext = applicationContext;
    }

    /** 维护各个实体类对应的CrudRepository对象，避免重复调用applicationContext.getBean方法影响性能 */
    private Map<String, BaseRepository<T, ID>> repositories = new HashMap<>();
    @PersistenceContext
    private BaseRepository<T, ID> getRepository() {
        // 1.获取实体对象对应的CrudRepository的bean名称，这里根据具体的命名风格来调整
        String entityClassName = getClass().getSimpleName(), beanName = StrUtil.lowerFirst(entityClassName) + "Repository";
        BaseRepository<T, ID> baseRepository = repositories.get(beanName);
        // 2.如果map中没有，从上下文环境获取，并放进map中
        if (Objects.isNull(baseRepository)) {
            baseRepository = (BaseRepository<T, ID>) applicationContext.getBean(beanName);
            repositories.put(beanName, baseRepository);
        }
        // 3. 返回
        return baseRepository;
    }

    /**
     * 保存当前对象
     * @return 保存后的当前对象
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public T save() {
        return getRepository().save((T) this);
    }

    /**
     * 根据当前对象的id获取对象
     * @return 查询到的对象
     */
    @SuppressWarnings("unchecked")
    public T findById() {
        return getRepository().findById(getId()).orElse(null);
    }

    /**
     * 删除当前对象
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public void delete() {
        getRepository().delete((T)this);
    }

    public abstract ID getId();
}
