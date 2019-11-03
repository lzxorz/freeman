package com.freeman.sys.controller;

import cn.hutool.core.convert.Convert;
import com.freeman.spring.data.utils.request.QueryRequest;
import com.freeman.common.result.R;
import com.freeman.common.base.controller.BaseController;
import com.freeman.common.utils.StrUtil;
import com.freeman.sys.domain.SysDict;
import com.freeman.sys.domain.SysDictItem;
import com.freeman.sys.service.ISysDictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/sys/dict")
public class SysDictController extends BaseController {

   @Autowired
   private ISysDictService dictService;

   /**  */
   @GetMapping
   @RequiresPermissions("dict:view")
   public R DictList(QueryRequest queryRequest, SysDict dict) {
       Page<SysDict> page = dictService.findAll(dict, queryRequest.getPageRequest());
       return R.ok(page);
   }

   /** 根据字典类型查询　选项 */
   @GetMapping("/dictItems")
   @RequiresPermissions("dict:view")
   public R DictItemList(SysDictItem dictItem) {
       Assert.hasText(dictItem.getType(), "字典类型必传参数");
       List<SysDictItem> all = dictService.findDictItems(dictItem);
       return R.ok("获取成功",all);
   }

   /** 新增字典 */
   //@Log(title = "新增字典")
   @PostMapping
   @RequiresPermissions("dict:add")
   public R addDict(@Valid SysDict dict) {
       dictService.save(dict);
       return R.ok("添加成功");
   }

    /** 新增字典 选项 */
    //@Log(title = "新增字典")
    @PostMapping("/dictItems")
    @RequiresPermissions("dict:add")
    public R addDictItem(@Valid SysDictItem dictItem) {
        dictService.saveDictItem(dictItem);
        return R.ok("添加成功");
    }


   /** 修改字典 */
   //@Log(title = "修改字典")
   @PutMapping
   @RequiresPermissions("dict:update")
   public R updateDict(@Valid SysDict dict) {
       dictService.save(dict);
       return R.ok("修改成功");
   }

    /** 修改字典 选项　*/
    //@Log(title = "修改字典")
    @PutMapping("/dictItem")
    @RequiresPermissions("dict:update")
    public R updateDictItem(@Valid SysDictItem dictItem) {
        dictService.saveDictItem(dictItem);
        return R.ok("修改成功");
    }

    /** 删除字典 */
    //@Log(title = "删除字典")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("dict:delete")
    public R deleteDict( @PathVariable String ids) {
        dictService.deleteById(Convert.toList(Long.class, ids.split(StrUtil.COMMA)));
        return R.ok("删除成功");

    }

    /** 删除字典　选项 */
    //@Log(title = "删除字典")
    @DeleteMapping("/dictItem/{ids}")
    @RequiresPermissions("dict:delete")
    public R deleteDictItem( @PathVariable String ids) {
        dictService.deleteDictItemById(Convert.toList(Long.class, ids.split(StrUtil.COMMA)));
        return R.ok("删除成功");
    }


    //  /** 导出Excel */
    /*@PostMapping("excel")
    @RequiresPermissions("dict:export")
    public void export(QueryRequest request, SysDict dict, HttpServletResponse response) {

    }*/
}
