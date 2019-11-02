package com.freeman.sys.service;

import com.freeman.common.base.service.IBaseService;
import com.freeman.sys.domain.SysRolePermission;

import java.util.List;

public interface ISysRolePermissionServie extends IBaseService<SysRolePermission, Long> {

    void deleteByRoleId(List<Long> roleIds);

    void deleteByPermissionId(List<Long> permissionIds);

    List<Long> findPermissionIdByRoleId(List<Long> roleIds);
}
