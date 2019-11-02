package com.freeman.sys.service;

import com.freeman.common.base.service.IBaseService;
import com.freeman.sys.domain.SysUserRole;

import java.util.List;

public interface ISysUserRoleService extends IBaseService<SysUserRole,Long> {

	void deleteByRoleId(Long[] roleIds);

	void deleteByUserId(Long[] userIds);


	List<Long> findUserIdByRoleId(Long[] roleIds);

}
