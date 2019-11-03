package com.freeman.sys.domain;


import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.BaseEntity;
import com.freeman.spring.data.utils.idgenerate.IdGen;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode()
//@ToString(callSuper = true)
@Entity @TableAlias("sll")
public class SysLoginLog extends BaseEntity<SysLoginLog,Long> {
    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long id;

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 登录时间 */
    private Date loginTime;

    /** 登录IP */
    private String ip;

    /** 登录地点 */
    private String location;

}
