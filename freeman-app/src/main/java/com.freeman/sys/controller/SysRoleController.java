package com.freeman.sys.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.freeman.common.base.controller.BaseController;
import com.freeman.common.dataPermission.DataPermUtil;
import com.freeman.common.log.Log;
import com.freeman.common.result.R;
import com.freeman.common.utils.StrUtil;
import com.freeman.spring.data.utils.request.QueryRequest;
import com.freeman.sys.domain.SysRole;
import com.freeman.sys.service.ISysRolePermissionServie;
import com.freeman.sys.service.ISysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {

    @Autowired
    private ISysRoleService roleService;
    @Autowired
    private ISysRolePermissionServie rolePermissionServie;

    /** 列表数据 */
    @GetMapping @RequiresPermissions("role:view")
    public R retrieveRole(SysRole role, QueryRequest queryRequest) {
        List<SysRole> all = roleService.findByNativeSql(DataPermUtil.dataScopeFilter(role), queryRequest.getSort());
        return R.ok("获取成功", all);
    }

    /** 根据code查到0条记录为true */
    @GetMapping("/check/{code}")
    public boolean checkRoleCode(@PathVariable("code") String code) {
        return roleService.checkRoleCode(code);
    }

    /** 获取角色拥有的权限IDS(菜单和按钮) */
    @GetMapping("/perms/{id}")
    public R retrievePermissionIds(@PathVariable("id") Long id) {
        List<Long> permissionIdByRoleId = rolePermissionServie.findPermissionIdByRoleId(CollectionUtil.newArrayList(id));
        return R.ok("获取成功",Convert.toList(String.class, permissionIdByRoleId));
    }

    /** 创建角色 */
    @Log(title = "创建角色")
    @PostMapping @RequiresPermissions("role:add")
    public R createRole(@Valid SysRole role) {
        roleService.save(role);
        return R.ok("创建成功");
    }

    /** 修改角色 */
    @Log(title = "修改角色")
    @PutMapping @RequiresPermissions("role:update")
    public R updateRole(SysRole role) {
        roleService.save(role);
        return R.ok("修改成功");
    }


    /** 根据IDS删除角色 */
    @Log(title = "删除角色")
    @DeleteMapping("/{ids}") @RequiresPermissions("role:delete")
    public R deleteRole(@PathVariable("ids") String ids) {
        List<Long> idList = Convert.toList(Long.class, ids.split(StrUtil.COMMA));
        roleService.deleteById(idList);
        return R.ok("修改成功");
    }

}
