package com.freeman.sys.service.impl;


import cn.hutool.core.convert.Convert;
import com.freeman.common.base.domain.Tree;
import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.common.constants.Constants;
import com.freeman.common.router.RouterMeta;
import com.freeman.common.router.VueRouter;
import com.freeman.common.utils.StrUtil;
import com.freeman.common.utils.TreeUtil;
import com.freeman.sys.domain.SysPermission;
import com.freeman.sys.repository.SysPermissionRepository;
import com.freeman.sys.service.ICacheService;
import com.freeman.sys.service.ISysPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@Service
public class SysPermissionServiceImpl extends BaseServiceImpl<SysPermissionRepository, SysPermission,Long> implements ISysPermissionService {

    @Autowired
    private ICacheService cacheService;

    public List<VueRouter> getUserRouters(Long userId) {
        List<SysPermission> menus = dao.findAllByUserIdAndType(userId, Constants.PERMISSION.TYPE_MENU);

        List<VueRouter> routes = menus.stream().map(menu -> {
            return VueRouter.builder()
                    .id(menu.getId())
                    .parentId(menu.getParentId())
                    .name(menu.getName())
                    .path(menu.getPath())
                    .component(menu.getComponent())
                    .icon(menu.getIcon())
                    .meta(new RouterMeta(true, null)).build();
        }).collect(Collectors.toList());

        return TreeUtil.buildVueRouter(routes);
    }

    @Override
    public Tree findTree(SysPermission permission) {
        //List<SysPermission> permissions = repository.findAll();
        List<Tree> treeList = dao.findTree(permission);
        return TreeUtil.build(treeList);
    }

    @Override
    public List<SysPermission> findAllByTemplate(SysPermission permission) {
        return dao.findAllByTemplate(permission);
    }

    @Override
    public Page<SysPermission> findAllByTemplate(SysPermission permission, Pageable pageable) {
        return dao.findAllByTemplate(permission, pageable);
    }

    @Override
    @Transactional
    public SysPermission save(SysPermission permission) {
        if (permission.isNew()){

        }else {
            // 删除权限及相关缓存
            cacheService.deletePermissionCache(Arrays.asList(permission.getId()));
        }

        if (permission.getParentId() == null)
            permission.setParentId(0L);
        if (Constants.PERMISSION.TYPE_BUTTON.equals(permission.getType())) {
            permission.setPath(null);
            permission.setIcon(null);
            permission.setComponent(null);
        }
        return super.save(permission);
    }

    @Override
    @Transactional
    public void deleteSelfAndChildrenById(List<Long> ids) {
        for (Long id : ids) {
            String childrenIds = dao.findChildrenById(id);
            // 自己和Children ID
            String joinIds = childrenIds != null ? (childrenIds + "," + id) : id.toString();
            List<Long> idList = Convert.toList(Long.class, joinIds.split(StrUtil.COMMA));
            dao.deleteSelfAndChildrenById(idList);
            // 删除权限及相关缓存
            cacheService.deletePermissionCache(idList);
        }
    }


}
