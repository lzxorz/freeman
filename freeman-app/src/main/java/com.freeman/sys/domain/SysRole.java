package com.freeman.sys.domain;


import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.AuditableEntity;
import com.freeman.spring.data.utils.idgenerate.IdGen;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data @Builder(toBuilder = true)
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity @TableAlias("sr")
public class SysRole extends AuditableEntity<SysRole,Long> {

    private static final long serialVersionUID = -7865236694755654924L;

    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long id;

    /** 上一级ID */
    private Long parentId;

    /** 角色名称 */
    @NotBlank(message = "{required}")
    @Size(max = 10, message = "{noMoreThan}")
    private String name;

    /** 角色编码 */
    private String code;

    /** 角色描述 */
    @Size(max = 255, message = "{noMoreThan}")
    private String remark;

    /** 排序 */
    private Integer sortNo;

    private String status;

    /** 角色--权限关系：多对多关系 */
    // @ManyToMany(fetch= FetchType.EAGER)
    // @JoinTable(name="sys_role_permission",joinColumns={@JoinColumn(name="role_id")},inverseJoinColumns={@JoinColumn(name="permission_id")})
    // private List<SysPermission> permissions;
    /** 角色的权限IDS, 创建、修改角色传参使用 */
    @Transient
    private String permissionIds;

    /** 数据范围 */
    private String dataScope;

    /** 可以查看数据的部门IDS */
    @Formula("(SELECT GROUP_CONCAT(srd.dept_id) FROM sys_role_dept srd WHERE srd.role_id = id)")
    private String dataDeptIds;


}