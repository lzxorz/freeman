package com.freeman.sys.repository;


import com.freeman.spring.data.repository.BaseRepository;
import com.freeman.sys.domain.SysRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SysRoleRepository extends BaseRepository<SysRole,Long> {
	String roleColumn = "sr.id, sr.parent_id, sr.name, sr.code, sr.remark, sr.sort_no, sr.status, sr.data_scope," +
			"(SELECT GROUP_CONCAT(srd.dept_id) as data_dept_ids FROM sys_role_dept srd WHERE srd.role_id = sr.id) as data_dept_ids," +
			"sr.create_time,sr.create_by,sr.update_by,sr.update_time";
	String roleFrom = "sys_role sr";


	@Query(value = "select r from SysRole r left join SysUserRole ur on r.id = ur.roleId where ur.id = :userId")
	List<SysRole> findByUserId(@Param("userId") Long userId);

	/** 根据角色ID集合 查询 数据权限部门IDS */
	@Query(value = "SELECT GROUP_CONCAT(srd.dept_id) as data_dept_ids FROM sys_role_dept srd WHERE srd.role_id = :roleId",nativeQuery = true)
	Set<Long> findDeptIdByRoleId(@Param("roleId")Long roleId);

	/** 根据角色ID集合 查询 数据权限部门IDS */
	@Query(value = "SELECT dept_id FROM sys_role_dept WHERE role_id IN (:roleIds)",nativeQuery = true)
	Set<Long> findDeptIdByRoleIdIn(@Param("roleIds")List<Long> roleIds);

}