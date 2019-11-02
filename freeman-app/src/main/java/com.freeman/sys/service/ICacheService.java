package com.freeman.sys.service;

import com.freeman.sys.domain.SysUser;
import com.freeman.spring.data.domain.BaseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ICacheService {

    void testConnect();

    <T extends BaseEntity> Object cachePut (String prefix, T t);

    SysUser getUserInfo(Long id);

    /** 根据用户ID查询用户缓存, 级联 角色缓存 级联 权限缓存 */
    SysUser getUser(Long id);

    /**** 根据用户ID查询角色IDS, 顺便 缓存用户角色关系 */
    //private List<Long> getRoleIdByUserId(Long userId);

    /** 根据角色IDS查询 角色缓存 级联 权限缓存 */
    //private List<SysRole> getRoles(Long... userId);

    /**** 根据角色ID查询权限IDS, 顺便 缓存角色权限关系 */
    //private List<Long> getPermissionIdByRoleId(Long userId);

    /** 根据权限IDS查询 权限 并缓存 */
    //private List<SysPermission> getPermissions(Long... userId);


    /** 根据ID 删除用户缓存 级联删除 用户角色关联缓存 */ // 修改用户时需要调用
    void deleteUserCache(Collection<Long> ids);

    /** 根据ID 删除角色缓存 级联删除 角色权限关联缓存 */ // 修改角色时需要调用
    void deleteRoleCache(Collection<Long> ids);

    /** 根据ID 删除权限缓存 */ // 修改权限时需要调用
    void deletePermissionCache(Collection<Long> ids);

    /** 加载、覆盖 所有 用户、角色、权限(三个表的数据) 缓存 */
    void loadCaches();


    /** 通过用户ID获取用户角色ID集合 */
    Set<String> getUserRoleIds(Long userId);

    /** 通过用户ID获取用户权限集合 */
    Set<String> getUserPermissions(Long userId);


    /** 获取redis在线用户 */
    List<Long> getOnlineUser();
    /** 保存到redis在线用户 */
    void appendOnline(Long userId);

}
