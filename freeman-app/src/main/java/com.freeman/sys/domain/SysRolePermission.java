package com.freeman.sys.domain;

import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.BaseEntity;
import com.freeman.sys.domain.id.RolePermissionId;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@IdClass(RolePermissionId.class)
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity @TableAlias("srp")
public class SysRolePermission extends BaseEntity<SysRolePermission,Long> {
	private static final long serialVersionUID = -89757L;

    @Id
    @Column(name="role_id")
    private Long roleId;

    @Id
    @Column(name="permission_id")
    private Long permissionId;

    @Override
    public Long getId() {
        return 123L;
    }
}