package com.freeman.sys.domain;


import com.alibaba.fastjson.annotation.JSONField;
import com.freeman.spring.data.annotation.TableAlias;
import com.freeman.common.auth.shiro.utils.ShiroUtil;
import com.freeman.common.constants.RegexpConstant;
import com.freeman.spring.data.domain.AuditableEntity;
import com.freeman.spring.data.domain.idgenerate.IdGen;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;


/**
 * 系统用户
 * @description 需要当前用户更多信息 ==> ShiroUtil#getCurrentUser
 * @author 刘志新
 * @email  lzxorz@163.com
 * @date   19-6-18 下午4:42
 * @Param
 * @return
 **/
@Data
@Builder(toBuilder = true)
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity @TableAlias("su")
public class SysUser extends AuditableEntity<SysUser,Long>{
    private static final long serialVersionUID = -7238846305983686701L;

    /** 默认密码 */
    public static final String DEFAULT_PASSWORD = "123456";
    /** 默认头像 */
    public static final String DEFAULT_AVATAR = "default.jpg";


    /** 用户ID */
    @Id
    @GeneratedValue(generator = "snowFlakeId")
    @GenericGenerator(name = "snowFlakeId", strategy = IdGen.TYPE)
    private Long id;

    /** 用户编号 */
    private String code;

    /** 用户名 */
    @Size(min = 4, max = 10, message = "{range}")
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
    @NotBlank(message = "{required}")
    private String sex;

    /** 年龄 */
    private Integer age;

    /** 手机号 */
    @Pattern(regexp = RegexpConstant.MOBILE, message = "{mobile}")
    private String phone;

    /** 生日 */
    private Date birthday;

    /** 邮箱 */
    @Email(message = "{email}") @Size(max = 50, message = "{noMoreThan}")
    private String email;

    /** 个人描述 */
    @Size(max = 100, message = "{noMoreThan}")
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
    @NotBlank(message = "{required}")
    private String status;

    @OneToOne
    @PrimaryKeyJoinColumn // 两个表的主键进行关联 ==等效==> @JoinColumn(name="id", referencedColumnName = "user_id")
    private SysUserConfig config;

    /** 所属公司ID */
    @Formula("(SELECT fnCommpanyIdByOrgId(dept_id) FROM DUAL)") // 使用这个,如果再写这个实体类原生sql查询会抛空指针异常
    private Long companyId;

    /** 所属部门 */
    /*@ManyToOne @JoinColumn(name="dept_id", referencedColumnName = "id")
    private SysOrg idCard;*/
    private Long deptId;
    @Formula("(SELECT so.name FROM sys_org so WHERE so.id = dept_id)")
    private String deptName;

    /** 拥有所有的角色,一个用户可以有多个角色 */
    /*@ManyToMany @JSONField(serialize = false) // 不序列化, 不缓存到用户数据中
    @JoinTable(name = "sys_user_role", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns ={@JoinColumn(name = "role_id") }) // 通过用户和角色关联表查询
    private List<SysRole> roles;*/

    /** 拥有的角色IDS */
    @Formula("(SELECT GROUP_CONCAT(sr.id) FROM sys_role sr LEFT JOIN sys_user_role sur ON sur.role_id = sr.id WHERE sur.user_id = id)")
    private String roleIds;
    /** 拥有的角色的名称 */
    @Formula("(SELECT GROUP_CONCAT(sr.name) FROM sys_role sr LEFT JOIN sys_user_role sur ON sur.role_id = sr.id WHERE sur.user_id = id)")
    private String roleNames;


    /** 拥有的角色的数据范围集合 */
    @Transient
    private Set<String> dataScope;

    /** 拥有的角色的数据部门集合(可以查看数据的部门) */
    @Transient
    private String dataDeptIds;

    /** 拥有的角色的权限标识 */
    @Transient
    private Set<String> permissions;




    /** 判断是不是超级管理员(id为1L) */
    @JSONField(serialize = false)
    public boolean isSuperAdmin(){
        return isSuperAdmin(this.id);
    }
    /** 判断是不是超级管理员(id为1L) */
    @JSONField(serialize = false)
    public static boolean isSuperAdmin(Long userId){
        return userId != null && 1L == userId;
    }

    /** 设置明文密码后, set盐值、并加密密码 */
    public SysUser encryptPasswd(String password) {
        this.password = password;
        ShiroUtil.encrypt(this);
        return this;
    }


}
