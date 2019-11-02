package com.freeman.sys.repository;


import com.freeman.sys.domain.SysRole;
import com.freeman.sys.domain.SysUser;
import com.freeman.sys.domain.SysUserRole;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserRoleRepository extends BaseRepository<SysUserRole, Long> {



    List<SysUserRole> findByUserId(Long userId);

    List<SysUserRole> findByRoleId(Long roleId);

    @Query(value = "SELECT su.userId FROM SysUserRole su WHERE su.roleId IN (:roleIds)")
    List<Long> findUserIdByRoleIdIn(@Param("roleIds")List<Long> roleIds);

    @Query("SELECT u FROM SysUser u ,SysUserRole ru WHERE u.id = ru.userId AND ru.roleId = :roleId")
    List<SysUser> findUserByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT r FROM SysRole r ,SysUserRole ur WHERE r.id = ur.roleId AND ur.userId = :userId")
    List<SysRole> findRolesByUserId(@Param("userId") Long userId);


    /** 根据用户Id删除该用户的角色关系 */
    @Modifying
    @Query("DELETE FROM SysUserRole WHERE userId = :userId")
    int deleteByUserId(@Param("userId") Long userId);

    /** 根据角色Id删除该角色的用户关系 */
    @Modifying
    @Query("DELETE FROM SysUserRole WHERE roleId = :roleId")
    int deleteByRoleId(@Param("roleId")Long roleId);


}