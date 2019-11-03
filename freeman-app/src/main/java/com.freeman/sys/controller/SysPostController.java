package com.freeman.sys.controller;


import cn.hutool.core.convert.Convert;
import com.freeman.common.base.controller.BaseController;
import com.freeman.common.log.Log;
import com.freeman.common.result.R;
import com.freeman.common.utils.StrUtil;
import com.freeman.spring.data.utils.request.QueryRequest;
import com.freeman.sys.domain.SysPost;
import com.freeman.sys.service.ISysPostService;
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
 * @Param
 * @return
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/sys/post")
public class SysPostController extends BaseController {


    @Autowired
    private ISysPostService postService;

   

    /**
     * 列表页
     * @author 刘志新
     * @email  lzxorz@163.com
     * @date   19-8-8 下午3:58
     * @Param
     * @return
     **/
    @GetMapping
    public R postList(SysPost post, QueryRequest queryRequest) {
      
        return R.ok("获取成功");
    }
    

    /**  */
    @Log(title = "新增岗位")
    @PostMapping
    @RequiresPermissions("post:add")
    public void addDept(@Valid SysPost post) {
        postService.save(post);
    }

    /**  */
    @Log(title = "修改岗位")
    @PutMapping
    @RequiresPermissions("post:update")
    public void updateDept(@Valid SysPost post) {
        postService.save(post);
    }

    /** 删除岗位 */
    @Log(title = "删除岗位")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("post:delete")
    public void deleteDepts(@NotBlank(message = "{required}") @PathVariable String ids) {
        postService.deleteById(Convert.toList(Long.class, ids.split(StrUtil.COMMA)));
    }


}
