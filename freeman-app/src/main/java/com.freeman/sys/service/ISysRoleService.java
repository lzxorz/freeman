package com.freeman.sys.service;

import com.freeman.spring.data.request.QueryRequest;
import com.freeman.common.base.service.IBaseService;
import com.freeman.sys.domain.SysRole;
import com.freeman.sys.domain.dto.SysRoleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ISysRoleService extends IBaseService<SysRole, Long> {

    Page<SysRole> findAll(SysRole role, QueryRequest queryRequest);

    boolean checkRoleCode(String code);

    void deleteRole(List<Long> roleIds);

    List<SysRole> findByUserId(Long userId);

    List<SysRole> findRoleDictItems(String dictType);

    List<SysRole> findAllByTemplate(SysRole sysRole);

    List<SysRoleDto> findAllByTemplate(SysRole sysRole, Sort sort);

    Page<SysRole> findAllByTemplate(SysRole sysRole, Pageable pageable);

}
