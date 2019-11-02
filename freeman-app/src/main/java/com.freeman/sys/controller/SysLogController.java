package com.freeman.sys.controller;


import com.freeman.common.log.Log;
import com.freeman.spring.data.request.QueryRequest;
import com.freeman.common.result.R;
import com.freeman.common.base.controller.BaseController;
import com.freeman.common.utils.StrUtil;
import com.freeman.sys.domain.SysLog;
import com.freeman.sys.service.ISysLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/sys/log")
public class SysLogController extends BaseController {

   @Autowired
   private ISysLogService logService;

   /**  */
   @GetMapping
   @RequiresPermissions("log:view")
   public R logList(QueryRequest queryRequest, SysLog sysLog) {
       Page<SysLog> all = logService.findPage(queryRequest,sysLog);
       return R.ok(all);
   }

   /** 删除日志 */
   @Log(title = "删除系统日志")
   @DeleteMapping("/{ids}")
   @RequiresPermissions("log:delete")
   public void deleteLogss(@NotBlank(message = "{required}") @PathVariable String ids) {
       String[] logIds = ids.split(StrUtil.COMMA);
       logService.deleteById(Arrays.stream(logIds).map(Long::valueOf).collect(Collectors.toList()));
   }

   /** 导出Excel */
   /*@PostMapping("excel")
   @RequiresPermissions("log:export")
   public void export(QueryRequest request, SysLog sysLog, HttpServletResponse response) {

   }*/
}
