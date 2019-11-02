package com.freeman.sys.controller;


import cn.hutool.core.convert.Convert;
import com.freeman.common.log.Log;
import com.freeman.common.result.R;
import com.freeman.common.base.controller.BaseController;
import com.freeman.common.base.domain.Tree;
import com.freeman.common.router.VueRouter;
import com.freeman.common.utils.StrUtil;
import com.freeman.sys.domain.SysPermission;
import com.freeman.sys.service.ISysPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/sys/permission")
public class SysPermissionController extends BaseController {


    @Autowired
    private ISysPermissionService permissionService;

    /** 用户路由 */
    @GetMapping("/{userId}")
    public List<VueRouter> userRouters(@PathVariable Long userId) {
        return permissionService.getUserRouters(userId);
    }

    /** treetable */
    @GetMapping
    @RequiresPermissions("menu:view")
    public R tree(SysPermission permission) {
        Tree all = permissionService.findTree(permission);
        return R.ok("获取成功", all);
    }

    /**  */
    @Log(title = "新增菜单/按钮")
    @PostMapping @PutMapping
    @RequiresPermissions("menu:add")
    public void add(@Valid SysPermission permission) {
        permissionService.save(permission);
    }

    /**  */
    @Log(title = "修改菜单/按钮")
    @PutMapping
    @RequiresPermissions("menu:update")
    public void update(@Valid SysPermission permission) { permissionService.save(permission); }

    /** 删除菜单/按钮及其Children */
    @Log(title = "删除菜单/按钮")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("menu:delete")
    public void delete(@NotBlank(message = "{required}") @PathVariable("ids") String ids) {
        List<Long> idList = Convert.toList(Long.class, ids.split(StrUtil.COMMA));
        permissionService.deleteSelfAndChildrenById(idList);

    }

}
