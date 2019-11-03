package com.freeman.sys.service.impl;


import cn.hutool.core.convert.Convert;
import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.common.cache.DictUtil;
import com.freeman.common.cache.redis.JedisDao;
import com.freeman.common.utils.StrUtil;
import com.freeman.spring.data.repository.NativeSqlQuery;
import com.freeman.spring.data.utils.request.QueryRequest;
import com.freeman.sys.domain.SysRole;
import com.freeman.sys.domain.SysRolePermission;
import com.freeman.sys.repository.SysRolePermissionRepository;
import com.freeman.sys.repository.SysRoleRepository;
import com.freeman.sys.service.ICacheService;
import com.freeman.sys.service.ISysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SysRoleServiceImpl extends BaseServiceImpl<SysRoleRepository, SysRole,Long> implements ISysRoleService {

    @Autowired
    private SysRoleRepository dao;
    @Autowired
    private ICacheService cacheService;
    @Autowired
    private SysRolePermissionRepository rolePermissionRepository;

    @Autowired(required = false)
    private JedisDao jedisDao;


    @Override @SuppressWarnings("Duplicates")
    public List<SysRole> findByNativeSql(SysRole sysRole, Sort sort) {
        String name = sysRole.getName();
        Object createTime = sysRole.getParams("createTime"); //传进来是数组
        NativeSqlQuery nativeSqlQuery = NativeSqlQuery.builder()
                .like(StrUtil.isNotBlank(name), "sr.name", "%"+name+"%")
                .between(Objects.nonNull(createTime), "date_format(sr.create_time,'%Y-%m-%d')", ((Object[])createTime)[0], ((Object[])createTime)[1])
                .eq("sr.status", DictUtil.getDictValue("sys_status", "有效", "1"))
                .conditionStrPart((String)sysRole.getParams("dataScope"))
                .orderBy("sr.sort_no asc");
        List<SysRole> reslutList = dao.findAllByNativeSql(nativeSqlQuery, SysRole.class);
        return reslutList;
    }

    @Override
    public Page<SysRole> findAll(SysRole role, QueryRequest queryRequest) {
        return dao.findAll(queryRequest.getPageRequest());
    }

    @Override
    public List<SysRole> findByUserId(Long userId) {
        return dao.findByUserId(userId);
    }

    @Override
    public boolean checkRoleCode(String code) {
        return 0L == dao.countByQL("SELECT 1 FROM SysRole sr WHERE sr.code = ?1", code);
    }

    @Override
    public SysRole save(SysRole role) {

        if (role.isNew()){

        }else{
            List<Long> ids = Arrays.asList(role.getId());
            // 根据角色ID删除角色及相关缓存
            cacheService.deleteRoleCache(ids);
            // 根据角色ID删除 用户-角色 关联数据
            rolePermissionRepository.deleteByRoleIdIn(ids);
            // TODO 删除角色字典
            // jedisDao.hdel(Constants.CACHE.DICT_PREFIX, dictType);
        }

        super.save(role);

        Long roleId = role.getId();

        // 保存角色-权限关系
        String permissionIds = role.getPermissionIds();
        if (StrUtil.isNotBlank(permissionIds)){
            List<SysRolePermission> rpList = new ArrayList();
            // 权限字符串IDS --> List<Long>, 遍历 --> 生成 角色权限对象 集合 --> 保存
            Convert.toList(Long.class, permissionIds.split(StrUtil.COMMA)).forEach(permissionId -> {
                rpList.add(SysRolePermission.builder().roleId(roleId).permissionId(permissionId).build());
            });
            rolePermissionRepository.saveAll(rpList);
        }

        return role;
    }


    @Override
    public void deleteRole(List<Long> roleIds) {
        // 根据角色ID删除角色及相关缓存
        cacheService.deleteRoleCache(roleIds);

        dao.deleteById(roleIds);

        // 删除 角色-权限 关联表 数据
        rolePermissionRepository.deleteByRoleIdIn(roleIds);

        // TODO 删除角色字典
        // jedisDao.hdel(Constants.CACHE.DICT_PREFIX, dictType);
    }

    /** 查询角色数据 转换成字典　, 修改角色时候　删除　这个字典　缓存　*/
    public List<SysRole> findRoleDictItems(String dictType) {

        NativeSqlQuery nativeSqlQuery = NativeSqlQuery.builder()
            .eq("status", DictUtil.getDictValue("sys_status", "有效", "1"))
            .orderBy("sort_no asc");
        List<SysRole> reslutList = dao.findAllByNativeSql(nativeSqlQuery, SysRole.class);

        return reslutList;
    }

}
