package com.freeman.sys.repository;


import com.freeman.sys.domain.SysUser;
import com.freeman.sys.domain.dto.SysUserDto;
import com.freeman.spring.data.annotation.TemplateQuery;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface SysUserRepository extends BaseRepository<SysUser,Long> {

    @TemplateQuery("findAllByTemplate")
    List<SysUserDto> findAllByTemplate();

    @TemplateQuery("findAllByTemplate")
    List<SysUserDto> findAllByTemplate(@Param("ids")List<Long> ids);


    @TemplateQuery("findAllByTemplate")
    List<Map<String,Object>> findAllByTemplate1();

    /*@TemplateQuery
    List<SysUser> findAllByTemplate(@Param("sysUser") SysUser sysUser);*/

    /*@TemplateQuery
    List<SysUser> findAllByTemplate(@Param("sysUser") SysUser sysUser, Sort sort);*/

    @TemplateQuery
    Page<SysUserDto> findAllByTemplate(@Param("sysUser") SysUser sysUser, Pageable pageable);

    /** 根据用户ID查询所属公司ID */
    @Query(value = "SELECT fnCommpanyIdByUid(:useId) FROM DUAL", nativeQuery = true)
    Long findCommpanyIdByUserId(@Param("useId")Long useId);

    /** 根据用户部门ID查询所属公司ID */
    @Query(value = "SELECT fnCommpanyIdByOrgId(:orgId) FROM DUAL", nativeQuery = true)
    Long findCommpanyIdByOrgId(@Param("orgId")Long orgId);


    SysUser findByUsername(String username);

    @Modifying
    @Query("UPDATE SysUser SET lastLoginTime = :nowTime WHERE id = :userId")
    void updateLastLoginTimeByUserId(@Param("nowTime") Date nowTime, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE SysUser SET avatar = :avatar WHERE id = :userId")
    void updataAvatar(@Param("avatar")String avatar, @Param("userId")Long userId);

    @Modifying
    @Query("UPDATE SysUser SET password = :password,salt = :salt WHERE id = :userId")
    void updatePassword(@Param("salt")String salt, @Param("password") String password, @Param("userId") Long userId);
}