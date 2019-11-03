package com.freeman.sys.domain;


import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.AuditableEntity;
import com.freeman.spring.data.utils.idgenerate.IdGen;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity @TableAlias("sp")
public class SysPermission extends AuditableEntity<SysPermission,Long> {

    private static final long serialVersionUID = 7187628714679791771L;

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long id;

    /** 上一级ID，顶级为0 */
    private Long parentId;


    /** 名称 */
    @NotBlank(message = "{required}")
    @Size(max = 10, message = "{noMoreThan}")
    private String name;

    /** 路径 */
    @Size(max = 50, message = "{noMoreThan}")
    private String path;

    /** 对应Vue组件 */
    @Size(max = 100, message = "{noMoreThan}")
    private String component;

    /** 权限 */
    @Size(max = 50, message = "{noMoreThan}")
    private String perms;

    /** 图标 */
    private String icon;

    /** 类型[1:菜单,2:按钮 ] */
    @NotBlank(message = "{required}")
    private String type;

    /** 排序 */
    private Integer sortNo;

    /** 是否隐藏 */
    private String hide;

    /** 描述 */
    private String description;

    public Long getId() {
        return id;
    }
}