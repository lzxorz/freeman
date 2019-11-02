package com.freeman.sys.domain.dto;

import com.freeman.common.base.domain.AuditableDto;
import com.freeman.sys.domain.SysUserConfig;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 *
 * @author: liuzhixin
 * @email: lzxorz@163.com
 * @date: 19-8-10
 * @version: 1.0
 */
@Data
public class SysUserDto extends AuditableDto {

    private static final long serialVersionUID = -6454630796271580447L;

    private Long userId;

    /** 用户编号 */
    private String code;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realname;

    /** 昵称 */
    private String nickname;

    /** 密码 */
    private String password;
    /** md5密码盐值 */
    private String salt;

    /** 用户头像 */
    private String avatar;

    /** 性别[1:男,2:女,2:保密] */
    private String sex;

    /** 年龄 */
    private Integer age;

    /** 手机号 */
    private String phone;

    /** 生日 */
    private Date birthday;

    /** 邮箱 */
    private String email;

    /** 个人描述 */
    private String description;

    /** 最近登录时间 */
    private Date lastLoginTime;

    /** QQ openid/unionid */
    private String qqid;
    /** 微信openid/unionid */
    private String wxid;
    /** 微博openid/unionid */
    private String wbid;

    /** 状态[0:锁定,1:正常] */
    private String status;

    private SysUserConfig config;

    /** 所属公司ID */
    private Long companyId;

    /** 所属部门 */
    private Long deptId;
    private String deptName;

    /** 拥有的角色IDS */
    private String roleIds;
    /** 拥有的角色的名称 */
    private String roleNames;


    /** 拥有的角色的数据范围集合 */
    private Set<String> dataScope;

    /** 拥有的角色的数据部门集合(可以查看数据的部门) */
    private String dataDeptIds;

    /** 拥有的角色的权限标识 */
    private Set<String> permissions;



    /** 判断是不是超级管理员(id为1L) */
    public boolean isSuperAdmin(){
        return isSuperAdmin(this.userId);
    }
    /** 判断是不是超级管理员(id为1L) */
    public static boolean isSuperAdmin(Long userId){
        return userId != null && 1L == userId;
    }

    public Long getId() { return userId; }
}
