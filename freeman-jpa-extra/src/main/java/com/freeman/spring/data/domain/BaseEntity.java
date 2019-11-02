package com.freeman.spring.data.domain;


import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@MappedSuperclass //类将不是一个完整的实体类，他将不会映射到数据库表，但是他的属性都将映射到其子类的数据库字段中。 //http://blog.sina.com.cn/s/blog_7085382f0100uk4p.htmlS
@EntityListeners({BaseEntityListener.class})//用于监听实体类操作的。
public abstract class BaseEntity<T extends Model, ID extends Serializable> extends Model<T,ID> {
    /** 数据逻辑删除  0:未删除(正常), 1:已删除 */
    // public static final String DEL_FLAG_NORMAL = "0";
    // public static final String DEL_FLAG_DELETE = "1";

    /** 是否 是:1, 否:0 */
    public static final String YES = "1";
    public static final String NO = "0";

    /*@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    protected ID id;*/

    /*//乐观锁
    @Version @Column(name="version",nullable=false,columnDefinition="int(20) comment '版本号'")
    protected int version;*/

    /** 数据逻辑删除  0:未删除(正常), 1:已删除 */
    /*@JSONField(serialize = false)
    @Column(name = "del_flag", columnDefinition = "CHAR DEFAULT '0'")
    private String delFlag;*/

    /** 请求参数 */
    @Transient @JSONField(serialize = false)
    private Map<String, Object> params;



    public BaseEntity setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }
    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    @JSONField(serialize = false)
    public boolean isNew() { return null==getId(); }

    /** 返回ID */
    @Override
    public abstract ID getId();



}
