package com.freeman.sys.domain;


import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.spring.data.domain.BaseEntity;
import com.freeman.sys.domain.id.UserRoleId;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@IdClass(UserRoleId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity @TableAlias("sur")
public class SysUserRole extends BaseEntity<SysUserRole, Long> {
	
	private static final long serialVersionUID = -89757L;


	/** 用户ID */
	@Id
	@Column(name = "user_id")
	private Long userId;

	/** 角色ID */
	@Id
	@Column(name = "role_id")
	private Long roleId;

	@Override
	public Long getId() {
		return 1L;
	}

}