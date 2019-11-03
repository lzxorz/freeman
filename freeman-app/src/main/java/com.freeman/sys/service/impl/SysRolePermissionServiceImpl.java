package com.freeman.sys.service.impl;

import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.sys.domain.SysRolePermission;
import com.freeman.sys.repository.SysRolePermissionRepository;
import com.freeman.sys.service.ISysRolePermissionServie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class SysRolePermissionServiceImpl extends BaseServiceImpl<SysRolePermissionRepository, SysRolePermission,Long> implements ISysRolePermissionServie {

	@Override
	@Transactional
	public void deleteByRoleId(List<Long> roleIds) {
		dao.deleteByRoleIdIn(roleIds);
	}

	@Override
	@Transactional
	public void deleteByPermissionId(List<Long> menuIds) {
		dao.deleteByPermissionIdIn(menuIds);
	}


	@Override
	public List<Long> findPermissionIdByRoleId(List<Long> roleIds) {
		return dao.findPermissionIdByRoleIdIn(roleIds);
	}
}
