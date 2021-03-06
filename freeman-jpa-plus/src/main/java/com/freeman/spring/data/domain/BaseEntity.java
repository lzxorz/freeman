package com.freeman.spring.data.domain;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.freeman.common.utils.StrUtil;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.*;


@MappedSuperclass //类将不是一个完整的实体类，他将不会映射到数据库表，但是他的属性都将映射到其子类的数据库字段中。
@EntityListeners({BaseEntityListener.class})//用于监听实体类操作的。
public abstract class BaseEntity<T extends Model, ID extends Serializable> extends Model<T,ID> {

    /*@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    protected ID id;*/

    /*//乐观锁
    @Version @Column(name="version",nullable=false,columnDefinition="int(20) comment '版本号'")
    protected int version;*/

    /**
     * 请求参数(只是查询需要用到,实体类属性不需要有)
     * 例如: params["ids"]=[10000,10010,10086]
     * 例如: params["createTime"]=["2049-01-01","2049-12-12"]
     */
    @Transient @JSONField(serialize = false)
    private Map<String, Object> params;



    public BaseEntity setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }
    public void putParam(String key, Object value) {
        if(this.params == null) {
            params = new HashMap<>();
        }
        this.params.put(key, value);
    }
    public Object getParams(String fieldName) {
        if (params == null) {
            return null;
        }
        Object values = params.get(fieldName);
        if (values == null) {
            return null;
        }
        if (values.getClass().isArray()){
            Object[] objects = ((Object[])values);
            if ( (StrUtil.endWithIgnoreCase(fieldName, "Time") || StrUtil.endWithIgnoreCase(fieldName, "Date")) && objects.length == 2) {
                return Arrays.asList( DateUtil.parse((String)objects[0]), DateUtil.parse((String)objects[1]) );
            }
            return Arrays.asList(objects);
        }

        return values;
    }

    @JSONField(serialize = false)
    public boolean isNew() { return null==getId(); }

    /** 返回ID */
    @Override
    public abstract ID getId();



}
