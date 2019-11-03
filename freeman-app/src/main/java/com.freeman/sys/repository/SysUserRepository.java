package com.freeman.sys.repository;


import com.freeman.spring.data.repository.BaseRepository;
import com.freeman.sys.domain.SysUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface SysUserRepository extends BaseRepository<SysUser,Long> {
    String userColumn = "su.id,su.code,su.username,su.realname,su.nickname,su.password,su.salt,su.avatar,su.sex,su.age,su.phone,su.birthday,su.email,su.description,su.last_login_time,su.qqid,su.wxid,su.wbid,su.status,su.create_by,su.create_time,su.update_by,su.update_time," +
            "suc.user_id  AS `config.userId`,suc.theme  AS `config.theme`,suc.layout  AS `config.layout`,suc.multi_page  AS `config.multiPage`,suc.fix_siderbar  AS `config.fixSiderbar`,suc.fix_header  AS `config.fixHeader`,suc.color  AS `config.color`," +
            "(SELECT fnCommpanyIdByUid(su.id) FROM DUAL) AS companyId," +
            "so.name AS deptName," +
            "GROUP_CONCAT(sr.id) AS roleIds," +
            "GROUP_CONCAT(sr.name) AS roleNames";
    String userFrom = "sys_user su " +
            "LEFT JOIN sys_user_config suc ON suc.user_id = su.id " +
            "LEFT JOIN sys_org so ON so.id = su.dept_id " +
            "LEFT JOIN sys_user_role sur ON sur.user_id = su.id " +
            "LEFT JOIN sys_role sr ON sr.id = sur.role_id";

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