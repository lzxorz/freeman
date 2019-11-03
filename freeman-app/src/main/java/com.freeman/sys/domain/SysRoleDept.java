package com.freeman.sys.domain;


import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.sys.domain.id.RoleDeptId;
import com.freeman.spring.data.domain.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@IdClass(RoleDeptId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity @TableAlias("srd")
public class SysRoleDept extends BaseEntity<SysRoleDept,Long> {

    private static final long serialVersionUID = -798033486264509053L;

    /** 角色ID */
    @Id
    @Column(name = "role_id")
    private Long roleId;

    /** 部门ID */
    @Id
    @Column(name = "dept_id")
    private Long deptId;

    @Override
    public Long getId() {
        return 1L;
    }


}