package com.freeman.sys.service;

import com.freeman.common.base.service.IBaseService;
import com.freeman.spring.data.utils.request.QueryRequest;
import com.freeman.sys.domain.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ISysUserService extends IBaseService<SysUser,Long> {

    Page<SysUser> findAll(SysUser entity, QueryRequest queryRequest);
    /**
     * 通过用户名查找用户
     *
     * @param username username
     * @return user
     */
    SysUser findByUsername(String username);

    /**
     * 查询用户详情，包括基本信息，用户角色，用户部门
     */

    /**
     * 更新用户登录时间
     *
     * @param id 用户ID
     */
    void updateLoginTime(Long id);


    /**
     * 更新用户头像
     *
     * @param avatar   用户头像
     * @param id 用户名
     */
    void updateAvatar(String avatar, Long id);

    /**
     * 注册用户
     *
     * @param username 用户名
     * @param password 密码
     */
    void regist(String username, String password);

    /**
     * 更新用户密码
     *
     * @param password 新密码
     * @param id 用户ID
     */
    void updatePassword(String password, Long id);

    /**
     * 重置密码
     *
     * @param userIds 用户ID集合
     */
    void resetPassword(List<Long> userIds);

    /**
     * 根据ID删除用户
     *
     * @param userIds 用户ID集合
     */
    void deleteById(List<Long> userIds);

}
