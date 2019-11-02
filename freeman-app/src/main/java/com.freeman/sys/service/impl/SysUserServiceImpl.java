package com.freeman.sys.service.impl;


import cn.hutool.core.util.StrUtil;
import com.freeman.common.auth.shiro.utils.ShiroUtil;
import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.common.dataSource.DbSource;
import com.freeman.common.constants.Constants;
import com.freeman.common.utils.BeanUtil;
import com.freeman.sys.domain.SysUser;
import com.freeman.sys.domain.dto.SysUserDto;
import com.freeman.sys.domain.SysUserRole;
import com.freeman.sys.repository.SysUserRepository;
import com.freeman.sys.repository.SysUserRoleRepository;
import com.freeman.sys.service.ICacheService;
import com.freeman.sys.service.ISysUserConfigService;
import com.freeman.sys.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SysUserServiceImpl extends BaseServiceImpl<SysUserRepository, SysUser,Long> implements ISysUserService {

    @Autowired
    private ISysUserConfigService userConfigService;
    @Autowired
    private ICacheService cacheService;
    @Autowired
    private SysUserRoleRepository userRoleRepository;


    @Override
    public SysUser findByUsername(String username) {
        return dao.findByUsername(username);
    }


    @Override @DbSource
    public Page<SysUserDto> findAll(SysUser user, Pageable pageable) {
        // 可以用, 但是不能拼接sql字符串
       /* Map<String, Object> params = user.getParams();
        Object createTime = null; //传进来是数组
        if (!ObjectUtils.isEmpty(params)) {
            createTime = params.get("createTime");
        }
        Long deptId = user.getDeptId();
        NativeQueryWrapper<SysUser> wrapper = new NativeQueryWrapper();
        Specification<SysUser> spec = wrapper.like(StrUtil.isNotBlank(user.getUsername()), "username", "%"+user.getUsername()+"%")
                .eq(Objects.nonNull(deptId), "deptId", deptId)
                .between(Objects.nonNull(createTime), "createTime", createTime)
                .eq(StrUtil.isNotBlank(user.getSex()), "sex", user.getSex())
                .eq(StrUtil.isNotBlank(user.getStatus()), "status", user.getStatus());
        return repository.findAll(spec,pageable);*/

        return dao.findAllByTemplate(user, pageable);
    }



    @Override
    @Transactional(readOnly=false)
    public SysUser save(SysUser user) {
        SysUser userdata = null;
        if (user.isNew()) {
            // 创建用户
            user.setAvatar(SysUser.DEFAULT_AVATAR);
            user.encryptPasswd(SysUser.DEFAULT_PASSWORD);

            // 创建用户默认的个性化配置
            userConfigService.initDefaultUserConfig(user.getId());
        }else {
            Long id = user.getId();
            userdata = dao.findById(id).get();
            if (Objects.nonNull(userdata)) {
                BeanUtil.copyProperties(user, userdata);
            }
            // 删除用户角色
            userRoleRepository.deleteByUserId(user.getId());
        }

        // 保存用户
        super.save(userdata);

        // 保存用户角色
        if (StrUtil.isNotBlank(user.getRoleIds())) {
            List<SysUserRole> urs = Arrays.stream(user.getRoleIds().split(",")).map(roleId -> new SysUserRole(user.getId(), Long.valueOf(roleId))).collect(Collectors.toList());
            this.userRoleRepository.saveAll(urs);
        }

        // 将用户相关信息保存到 Redis中
        cacheService.cachePut(Constants.CACHE.USER_PREFIX, user);
        return userdata != null ? userdata : user;
    }


    @Override
    @Transactional
    public void updateAvatar(String avatar, Long id) {
        dao.updataAvatar(avatar,id);
        // 清用户缓存
        cacheService.deleteUserCache(Arrays.asList(id));

    }

    @Override
    @Transactional
    public void updateLoginTime(Long id) {
        dao.updateLastLoginTimeByUserId(new Date(), id);
        // 清用户缓存
        cacheService.deleteUserCache(Arrays.asList(id));

    }

    @Override
    @Transactional
    public void updatePassword(String password, Long id) {
        String salt = ShiroUtil.randomSalt();
        dao.updatePassword(salt, ShiroUtil.encrypt(password, salt),id);
        // 清用户缓存
        cacheService.deleteUserCache(Arrays.asList(id));
    }

    @Override
    @Transactional
    public void resetPassword(List<Long> userIds){
        for (Long id : userIds) {
            String salt = ShiroUtil.randomSalt();
            dao.updatePassword(salt, ShiroUtil.encrypt(SysUser.DEFAULT_PASSWORD, salt),id);
            // 清用户缓存
            cacheService.deleteUserCache(Arrays.asList(id));
        }

    }

    @Override
    @Transactional
    public void regist(String username, String password) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.encryptPasswd(password);
        user.setAvatar(SysUser.DEFAULT_AVATAR);
        user.setCreateTime(new Date());
        user.setStatus("1");// User.STATUS_VALID
        user.setSex("2"); // User.SEX_UNKNOW
        user.setDescription("注册用户");
        user.save();

        SysUserRole ur = new SysUserRole();
        ur.setUserId(user.getId());
        ur.setRoleId(2L); // 注册用户角色 ID
        userRoleRepository.save(ur);

        // 创建用户默认的个性化配置
        userConfigService.initDefaultUserConfig(user.getId());
        // 将用户相关信息保存到 Redis中
        //userCache.loadCaches(user);

    }

    @Override
    @Transactional
    public void deleteById(List<Long> userIds) {
        // 先删除相应的缓存
        cacheService.deleteUserCache(userIds);
        dao.deleteById(userIds);
    }

}
