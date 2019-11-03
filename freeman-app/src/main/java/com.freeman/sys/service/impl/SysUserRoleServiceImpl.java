package com.freeman.sys.service.impl;

import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.sys.domain.SysUserRole;
import com.freeman.sys.repository.SysUserRoleRepository;
import com.freeman.sys.service.ISysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;


@Transactional(readOnly = true)
@Service
public class SysUserRoleServiceImpl extends BaseServiceImpl<SysUserRoleRepository, SysUserRole,Long> implements ISysUserRoleService {

	@Override
	@Transactional
	public void deleteByRoleId(Long[] roleIds) {
		Arrays.stream(roleIds).forEach(id -> dao.deleteByRoleId(Long.valueOf(id)));
	}

	@Override
	@Transactional
	public void deleteByUserId(Long[] userIds) {
		Arrays.stream(userIds).forEach(id -> dao.deleteByUserId(Long.valueOf(id)));
	}

	@Override
	public List<Long> findUserIdByRoleId(Long[] roleIds) {
		if (ObjectUtils.isEmpty(roleIds)) return null;
		return dao.findUserIdByRoleIdIn(Arrays.asList(roleIds));
	}

}
