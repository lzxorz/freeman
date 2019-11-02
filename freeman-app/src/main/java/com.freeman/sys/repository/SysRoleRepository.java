package com.freeman.sys.repository;


import com.freeman.sys.domain.SysRole;
import com.freeman.sys.domain.dto.SysRoleDto;
import com.freeman.spring.data.annotation.TemplateQuery;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SysRoleRepository extends BaseRepository<SysRole,Long> {

	@TemplateQuery("findAllByTemplate")
	List<SysRoleDto> findAllByTemplate();

	@TemplateQuery("findAllByTemplate")
	List<SysRoleDto> findAllByTemplate(@Param("ids")List<Long> ids);

	@TemplateQuery
    List<SysRole> findAllByTemplate(SysRole sysRole);

	@TemplateQuery
    List<SysRoleDto> findAllByTemplate(SysRole sysRole, Sort sort);

	@TemplateQuery
	Page<SysRole> findAllByTemplate(SysRole sysRole, Pageable pageable);

	@Query(value = "select r from SysRole r left join SysUserRole ur on r.id = ur.roleId where ur.id = :userId")
	List<SysRole> findByUserId(@Param("userId") Long userId);


	/** 根据角色ID集合 查询 数据权限部门IDS */
	@Query(value = "SELECT GROUP_CONCAT(srd.dept_id) as data_dept_ids FROM sys_role_dept srd WHERE srd.role_id = :roleId",nativeQuery = true)
	Set<Long> findDeptIdByRoleId(@Param("roleId")Long roleId);

	/** 根据角色ID集合 查询 数据权限部门IDS */
	@Query(value = "SELECT dept_id FROM sys_role_dept WHERE role_id IN (:roleIds)",nativeQuery = true)
	Set<Long> findDeptIdByRoleIdIn(@Param("roleIds")List<Long> roleIds);

}