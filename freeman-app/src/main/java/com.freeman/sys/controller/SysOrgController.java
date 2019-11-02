package com.freeman.sys.controller;


import cn.hutool.core.convert.Convert;
import com.freeman.common.log.Log;
import com.freeman.common.dataPermission.DataPermissionUtil;
import com.freeman.spring.data.request.QueryRequest;
import com.freeman.common.result.R;
import com.freeman.common.base.controller.BaseController;
import com.freeman.common.base.domain.Tree;
import com.freeman.common.utils.StrUtil;
import com.freeman.sys.domain.SysOrg;
import com.freeman.sys.service.ISysOrgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 *
 * 组织机构管理
 * 基本的增删改查
 * 根据用户的数据范围 查询树结构(支持传pid, lazy load)
 * @author 刘志新
 * @email  lzxorz@163.com
 * @date   19-8-8 下午3:53
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/sys/org")
public class SysOrgController extends BaseController {


    @Autowired
    private ISysOrgService orgService;

    /** 测试jpa调用mysql存储过程 */
    @GetMapping("/test/{pid}")
    public void test(@PathVariable Long pid) {
        orgService.testStoredProcedure(pid);
    }

    /**
     * 列表页
     * treeTable展示, 使用模板查询 List<SysOrg> 结果 转换成 Tree
     * @author 刘志新
     * @email  lzxorz@163.com
     * @date   19-8-8 下午3:58
     * @Param
     * @return
     **/
    @GetMapping
    public R orgList(SysOrg org, QueryRequest queryRequest) {
        Tree orgTree = orgService.findOrgTableTree(DataPermissionUtil.dataScopeFilter(org), queryRequest.getSort());
        return R.ok("获取成功", orgTree);
    }

    /**
     * 树选择组件
     * @description  treeSelect展示, 使用模板查询
     * @author 刘志新
     * @email  lzxorz@163.com
     * @date   19-8-8 下午3:58
     * @Param
     * @return
     **/
    @GetMapping("/treeSelect")
    public R orgSelectTree(Boolean lazy, Long pid) {
        Tree orgTree = orgService.findOrgSelectTree(DataPermissionUtil.dataScopeFilter(new SysOrg()));
        return R.ok("获取成功", orgTree);
    }

    /**  */
    @Log(title = "新增部门")
    @PostMapping
    @RequiresPermissions("org:add")
    public void addDept(@Valid SysOrg org) {
        orgService.save(org);
    }

    /**  */
    @Log(title = "修改部门")
    @PutMapping
    @RequiresPermissions("org:update")
    public void updateDept(@Valid SysOrg org) {
        orgService.save(org);
    }

    /** 删除部门 */
    @Log(title = "删除部门")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("org:delete")
    public void deleteDepts(@NotBlank(message = "{required}") @PathVariable String ids) {
        orgService.deleteById(Convert.toList(Long.class, ids.split(StrUtil.COMMA)));
    }


}
