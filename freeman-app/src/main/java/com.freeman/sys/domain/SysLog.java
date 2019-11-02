package com.freeman.sys.domain;


import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.BaseEntity;
import com.freeman.spring.data.domain.idgenerate.IdGen;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
//@ToString(callSuper = true)
@Entity @TableAlias("sl")
public class SysLog extends BaseEntity<SysLog,Long> {

    private static final long serialVersionUID = -8878596941954995444L;

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long id;

    /** 操作人 */
    private Long userId;
    private String username;

    private String  os;
    private String  browser;
    private String  browserVersion;

    /** IP地址 */
    private String ip;
    /** 操作地点 */
    private String location;


    /** 操作描述 */
    private String operation;


    /** 请求资源 */
    private String uri;

    /** 执行方法 */
    private String method;

    /** 方法参数 */
    private String parameter;

    /**  */
    private String errorMsg;

    /** 耗时（毫秒） */
    private Long time;

    /** 操作时间 */
    private Date createTime;


}