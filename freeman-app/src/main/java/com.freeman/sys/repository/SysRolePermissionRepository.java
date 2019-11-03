package com.freeman.sys.repository;


import com.freeman.sys.domain.SysRolePermission;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysRolePermissionRepository extends BaseRepository<SysRolePermission,Long> {

    @Query(value = "SELECT srp.permissionId FROM SysRolePermission srp WHERE srp.roleId IN (:roleIds)")
    List<Long> findPermissionIdByRoleIdIn(@Param("roleIds") List<Long> roleIds);

    @Query(value = "SELECT srp.roleId FROM SysRolePermission srp WHERE srp.permissionId IN (:permissionIds)")
    List<Long> findRoleIdByPermissionIdIn(@Param("permissionIds") List<Long> permissionIds);

    void deleteByRoleIdIn(@Param("roleIds")List<Long> roleIds);

    void deleteByPermissionIdIn(@Param("permissionIds") List<Long> permissionIds);

}