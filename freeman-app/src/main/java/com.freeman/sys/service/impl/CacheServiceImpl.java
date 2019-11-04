package com.freeman.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.freeman.common.cache.redis.JedisDao;
import com.freeman.common.constants.Constants;
import com.freeman.spring.data.repository.NativeSqlQuery;
import com.freeman.sys.domain.*;
import com.freeman.sys.repository.*;
import com.freeman.sys.service.ICacheService;
import com.freeman.spring.data.domain.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户角色权限的缓存
 */
@Slf4j
@Service("cacheService")
//@Transactional(readOnly = true)
public class CacheServiceImpl implements ICacheService {

    @Autowired
    private JedisDao jedisDao;

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private SysRoleRepository roleRepository;

    @Autowired
    private SysUserRoleRepository userRoleRepository;

    @Autowired
    private SysPermissionRepository permissionRepository;

    @Autowired
    private SysRolePermissionRepository rolePermissionRepository;



    public void testConnect() {
        jedisDao.exists("test");
    }

    // =================================基础缓存方法 (单一资源 根据 ID/IDS) ==================================== //

    /** 先查询是否已经有缓存，有 就使用缓存，没有 数据库查询并缓存 ==> 返回数据 */
    public <T extends BaseEntity> List<T> cacheable (Function<List<Long>, List<T>>  daoFunc, Class<T> clazz, String prefix, Collection<Long> ids) {


        if (ObjectUtils.isEmpty(ids)){
            return cachePut( daoFunc, prefix, null);
        }else{
            List<T> resList = new ArrayList();
            for(Iterator<Long> iterator = ids.iterator(); iterator.hasNext(); ) {
                Long id = iterator.next();
                String jsonStr = jedisDao.hget(prefix, String.valueOf(id));
                if (StrUtil.isNotBlank(jsonStr)) {
                    resList.add(JSON.parseObject(jsonStr, clazz));
                    iterator.remove();
                }
            }

            if (!ObjectUtils.isEmpty(ids)){
                List<T> ts = cachePut( daoFunc, prefix, ids);
                resList.addAll(ts);
            }

            return resList;
        }
    }

    /** 数据库查询并缓存 ==> 返回数据 */
    public <T extends BaseEntity> List<T> cachePut (Function<List<Long>,List<T>>  daoFunc, String prefix, Collection<Long> ids) {
        List<T> all = daoFunc.apply(CollectionUtil.list(false, ids));
        // if (ObjectUtils.isEmpty(all))
        for (BaseEntity entity : all) {
            cachePut(prefix, entity);
        }
        return all;
    }


    /** 资源更新到缓存 */
    public <T extends BaseEntity> T cachePut (String prefix, T entity) {
        if (!ObjectUtils.isEmpty(entity)){
            jedisDao.hset(prefix, String.valueOf(entity.getId()), JSON.toJSONString(entity));
        }
        return entity;
    }

    /** 删除缓存 */
    public void cacheEvict (String prefix, Collection<Long> ids) {
        if (ObjectUtils.isEmpty(ids)) return;
        jedisDao.hdel(prefix, CollectionUtil.join(ids, ","));
    }

    // ====================================用户、角色、权限 缓存================================= //

    public SysUser getUserInfo(Long id) {
        if (id == null) return null;
        List<SysUser> users = cacheable( (List<Long> ids) -> {
            NativeSqlQuery nativeSqlQuery = NativeSqlQuery.builder()
                .select(SysUserRepository.userColumn).from(SysUserRepository.userFrom).in("su.id", ids);
            List<SysUser> allUser = userRepository.findAllByNativeSql(nativeSqlQuery,SysUser.class);
            return allUser;
        }, SysUser.class, Constants.CACHE.USER_PREFIX, CollectionUtil.newArrayList(id));

        if (ObjectUtils.isEmpty(users)) return null;

        return users.get(0);
    }


    /** 根据用户ID查询用户(包含commpanyId,deptId,deptName,roleIds,roleNames), 级联 角色; 所拥有角色的数据权限部门IDS集合 */ // 级联 权限
    public SysUser getUser(Long id) {

        SysUser user = getUserInfo(id);
        if (ObjectUtils.isEmpty(user)) return null;

        // 用户的角色IDS
        Set<Long> roleIds = getRoleIdByUserId(id);
        user.setRoleIds(CollectionUtil.join(roleIds,","));

        // 用户的角色
        List<SysRole> roles = getRoles(CollectionUtil.newArrayList(roleIds));
        if (CollectionUtil.isNotEmpty(roles)) {
            // 用户的角色的 NAMES、数据范围集合、数据权限部门IDS集合
            StringJoiner roleNames = new StringJoiner(",");
            final Set<String> dataScope = CollectionUtil.newHashSet();
            final Set<Long> dataDeptIds = CollectionUtil.newHashSet();
            roles.forEach(r -> {
                roleNames.add(r.getName());
                String scope = r.getDataScope();
                if (StrUtil.isNotBlank(scope))
                    dataScope.add(scope);
                String deptIds = r.getDataDeptIds();
                if (StrUtil.isNotBlank(deptIds)) {
                    Set<Long> sets = Arrays.stream(deptIds.split(",")).map(Long::valueOf).collect(Collectors.toSet());
                    dataDeptIds.addAll(sets);
                }
            });
            user.setRoleNames(roleNames.toString());
            user.setDataScope(dataScope);
            user.setDataDeptIds(CollectionUtil.join(dataDeptIds, ","));
        }

        // 权限标识集合
        user.setPermissions(getUserPermissions(id));
        return user;
    }


    /** 根据角色IDS查询 角色(包含角色的数据范围 数据权限部门IDS,不包含角色的权限) */
    private List<SysRole> getRoles(List<Long> roleIds) {
        if (ObjectUtils.isEmpty(roleIds)) return null;
        List<SysRole> roles = cacheable((List<Long> ids) -> {
            NativeSqlQuery nativeSqlQuery = NativeSqlQuery.builder()
                .select(SysRoleRepository.roleColumn).from(SysRoleRepository.roleFrom).in("id", ids).build();
            List<SysRole> all = roleRepository.findAllByNativeSql(nativeSqlQuery,SysRole.class);
            return all;
        }, SysRole.class, Constants.CACHE.ROLE_PREFIX, roleIds);

        return roles;
    }

    /** 根据权限IDS查询权限 */
    private List<SysPermission> getPermissions(List<Long> permissionIds) {
        if (ObjectUtils.isEmpty(permissionIds)) return null;
        List<SysPermission> permissions = cacheable((List<Long> ids) -> {
            NativeSqlQuery nativeSqlQuery = NativeSqlQuery.builder()
                    .select(SysPermissionRepository.permissionColumn).from(SysPermissionRepository.permissionFrom).in("id", ids).build();
            List<SysPermission> all = permissionRepository.findAllByNativeSql(nativeSqlQuery,SysPermission.class);

            return all;
        },  SysPermission.class, Constants.CACHE.PERMISSION_PREFIX, permissionIds);
        return permissions;
    }

    /** 根据用户ID查询角色IDS, 顺便 缓存用户角色关系 */
    private Set<Long> getRoleIdByUserId(Long userId) {
        if (ObjectUtils.isEmpty(userId)) return null;
        Set<Long> roleIds = CollectionUtil.newHashSet();
        String jsonStr = jedisDao.hget(Constants.CACHE.USER_ROLE_PREFIX, String.valueOf(userId));
        if (StrUtil.isNotBlank(jsonStr)) {
            roleIds.addAll(JSON.parseArray(jsonStr, Long.class)); //new TypeReference<List<Long>>() {}
        }else {
            // 数据库查询,缓存用户角色关系
            List<Long> ids = userRoleRepository.findByUserId(userId).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            jedisDao.hset(Constants.CACHE.USER_ROLE_PREFIX, String.valueOf(userId), JSON.toJSONString(ids));
            roleIds.addAll(ids);
        }
        return roleIds;
    }

    /** 根据角色IDS查询权限IDS, 顺便 缓存角色权限关系 */
    private Set<Long> getPermissionIdByRoleId(Long... roleIds) {
        if (ObjectUtils.isEmpty(roleIds)) return null;
        Set<Long> permissionIds = CollectionUtil.newHashSet();

        for (Long roleId : roleIds) {
            String jsonStr = jedisDao.hget(Constants.CACHE.ROLE_PERMISSION_PREFIX, String.valueOf(roleId));
            if (StrUtil.isNotBlank(jsonStr)) {
                permissionIds.addAll(JSON.parseArray(jsonStr, Long.class));
            }else {
                // 数据库查询,缓存角色权限关系
                List<Long> ids = rolePermissionRepository.findPermissionIdByRoleIdIn(Arrays.asList(roleId));
                jedisDao.hset(Constants.CACHE.ROLE_PERMISSION_PREFIX, String.valueOf(roleId), JSON.toJSONString(ids));
                permissionIds.addAll(ids);
            }
        }
        return permissionIds;
    }

    /** 通过用户ID获取用户角色IDS集合 */ // Shiro 授权 调用
    public Set<String> getUserRoleIds(Long userId) {
        if (ObjectUtils.isEmpty(userId)) return null;
        Set<Long> roleIds = getRoleIdByUserId(userId);
        Set<String> resultSet = roleIds.stream().map(rid -> rid.toString()).collect(Collectors.toSet());
        return resultSet;
    }

    /** 通过用户ID获取用户权限标识(sys:user:view)集合 */ //Shiro 授权 调用
    public Set<String> getUserPermissions(Long userId) {
        if (ObjectUtils.isEmpty(userId)) return null;
        Set<String> resultSet = CollectionUtil.newHashSet();
        // 获取用户对应的 角色 IDS
        Set<Long> roleIds = getRoleIdByUserId(userId);
        // 获取某个角色 对应的权限 IDS
        Set<Long> permissionIds;
        // 获取权限对象
        List<SysPermission> permissionList;
        // 权限标识,例如： sys:user:view
        String perms;
        if (!ObjectUtils.isEmpty(roleIds)){
            for (Long roleId : roleIds) {
                permissionIds = getPermissionIdByRoleId(roleId);
                permissionList = getPermissions(CollectionUtil.newArrayList(permissionIds));
                for (SysPermission permission : permissionList) {
                    perms = permission.getPerms();
                    if (StrUtil.isNotBlank(perms)){
                        resultSet.add(perms);
                    }
                }
            }
        }
        return resultSet;
    }

    /** 加载、覆盖 所有 用户、角色、权限(三个表的数据) 缓存 */
    public void loadCaches() {
        cachePut((List<Long> ids) -> {
            NativeSqlQuery nativeSql = NativeSqlQuery.builder()
                .select(SysUserRepository.userColumn).from(SysUserRepository.userFrom).groupBy("su.id").build();
            List<SysUser> sysUsers = userRepository.findAllByNativeSql(nativeSql, SysUser.class);
            return sysUsers;
        }, Constants.CACHE.USER_PREFIX, null);

        cachePut((List<Long> ids) -> {
            NativeSqlQuery nativeSql = NativeSqlQuery.builder()
                .select(SysRoleRepository.roleColumn).from(SysRoleRepository.roleFrom).build();
            List<SysRole> sysRoles = roleRepository.findAllByNativeSql(nativeSql, SysRole.class);
            return sysRoles;
        }, Constants.CACHE.ROLE_PREFIX, null);

        cachePut((List<Long> ids) -> {
            NativeSqlQuery nativeSql = NativeSqlQuery.builder()
                .select(SysPermissionRepository.permissionColumn).from(SysPermissionRepository.permissionFrom).build();
            List<SysPermission> sysPermissions = permissionRepository.findAllByNativeSql(nativeSql, SysPermission.class);
            return sysPermissions;
        }, Constants.CACHE.PERMISSION_PREFIX, null);
    }


    /** 根据ID 删除用户缓存 用户角色关联缓存 */ // 修改/删除用户时需要调用
    @Override
    public void deleteUserCache(Collection<Long> ids) {
        if (ObjectUtils.isEmpty(ids)) return;
        cacheEvict(Constants.CACHE.USER_PREFIX, ids);
        cacheEvict(Constants.CACHE.USER_ROLE_PREFIX, ids);
    }

    /** 根据ID 删除角色缓存 删除角色权限关联缓存 删除对应用户及用户角色关联缓存数据 */ // 修改/删除角色时需要调用
    @Override
    public void deleteRoleCache(Collection<Long> ids) {
        if (ObjectUtils.isEmpty(ids)) return;
        cacheEvict(Constants.CACHE.ROLE_PREFIX, ids);
        cacheEvict(Constants.CACHE.ROLE_PERMISSION_PREFIX, ids);
        List<Long> userIds = userRoleRepository.findUserIdByRoleIdIn(CollectionUtil.list(false, ids));
        // 删除角色对应的用户相关...
        deleteUserCache(userIds);

    }

    /** 根据ID 删除权限缓存 删除权限对应的角色及相关缓存 */ // 修改/删除权限时需要调用
    @Override
    public void deletePermissionCache(Collection<Long> ids) {
        if (ObjectUtils.isEmpty(ids)) return;
        cacheEvict(Constants.CACHE.PERMISSION_PREFIX, ids);
        // 删除权限对应的角色及相关缓存
        List<Long> roleIds = rolePermissionRepository.findRoleIdByPermissionIdIn(CollectionUtil.list(false, ids));
        deleteRoleCache(roleIds);
    }

    /** 获取redis在线用户 */
    @Override
    public List<Long> getOnlineUser() {
        Set<String> strings = jedisDao.zrangeByScore(Constants.USER.ACTIVE_USERS_ZSET_PREFIX, "-inf", Convert.toStr(System.currentTimeMillis()));
        return Convert.toList(Long.class, strings);
    }

    /** 保存到redis在线用户 */
    public void appendOnline(Long userId) {
        if (userId == null) log.error(" tell me why?");
        // zset 存储登录用户, score为过期时间戳
        jedisDao.zadd(Constants.USER.ACTIVE_USERS_ZSET_PREFIX, Convert.toDouble(System.currentTimeMillis()), String.valueOf(userId));
    }

}
