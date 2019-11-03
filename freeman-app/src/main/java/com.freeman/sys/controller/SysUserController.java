package com.freeman.sys.controller;

import cn.hutool.core.convert.Convert;
import com.freeman.common.auth.shiro.utils.ShiroUtil;
import com.freeman.common.base.controller.BaseController;
import com.freeman.common.dataPermission.DataPermissionUtil;
import com.freeman.common.log.Log;
import com.freeman.common.result.R;
import com.freeman.common.utils.StrUtil;
import com.freeman.spring.data.utils.request.QueryRequest;
import com.freeman.sys.domain.SysUser;
import com.freeman.sys.domain.SysUserConfig;
import com.freeman.sys.service.ISysUserConfigService;
import com.freeman.sys.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController {

    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysUserConfigService userConfigService;


    // ------------- @GetMapping -------------
    @Log(title = "查看用户")
    @GetMapping
    @RequiresPermissions("user:view")
    public R userList(SysUser user, QueryRequest queryRequest) {
        Page<SysUser> page = userService.findAll(DataPermissionUtil.dataScopeFilter(user), queryRequest.getPageRequest());
        return R.ok("获取成功", page);
    }


    @GetMapping("/{userId}")
    public SysUser detail(@NotBlank(message = "{required}") @PathVariable Long userId) {
        return userService.findById(userId).get();
    }

    @GetMapping("/check/{username}")
    public boolean checkUsername(@PathVariable("username") String username) {
        return userService.findByUsername(username) == null;
    }

    @GetMapping("password/check")
    public boolean checkPassword(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) {
        SysUser user = userService.findByUsername(username);
        String encryptPassword = ShiroUtil.encrypt(password, user.getSalt());
        if (user != null)
            return StrUtil.equals(user.getPassword(), encryptPassword);
        else
            return false;
    }


    // ------------- @PostMapping -------------
    @Log(title = "新增用户")
    @PostMapping
    @RequiresPermissions("user:add")/*@Valid*/
    public void addUser(SysUser user){
            userService.save(user);
    }

    // ------------- @PutMapping -------------
    @Log(title = "修改用户")
    @PutMapping
    @RequiresPermissions("user:update")
    public void updateUser(@Valid SysUser user){
        userService.save(user);
    }

    /** 修改头像失败 */
    @PutMapping("/avatar")
    public void updateAvatar(
            @NotBlank(message = "{required}") Long userId,
            @NotBlank(message = "{required}") String avatar){
        //TODO userService.updateAvatar(username, avatar);
    }

    /** 修改密码 */
    @PutMapping("/password")
    public void updatePassword(
            @NotBlank(message = "{required}") Long userId,
            @NotBlank(message = "{required}") String password){
        //TODO userService.updatePassword(username, password);
    }


    /** 重置用户密码 */
    @PutMapping("/password/reset/{ids}")
    @RequiresPermissions("user:reset")
    public void resetPassword(@PathVariable("ids") String ids){
        List<Long> userIds = StrUtil.splitTrim(ids, ",").stream().map(Long::valueOf).collect(Collectors.toList());
        userService.resetPassword(userIds);
    }

    /** 修改个性化配置失败 */
    @PutMapping("/config")
    public void updateUserConfig(@Valid SysUserConfig userConfig){
        userConfigService.save(userConfig);

    }


    // ------------- @DeleteMapping -------------
    @Log(title = "删除用户")
    @DeleteMapping("/{ids}")
    @RequiresPermissions("user:delete")
    public void deleteUsers(@NotBlank(message = "{required}") @PathVariable String ids){
        List<Long> idList = Convert.toList(Long.class, ids.split(StrUtil.COMMA));
        userService.deleteById(idList);
    }


    // ------------- 导出Excel -------------
    /** 导出Excel */
    @PostMapping("excel")
    @RequiresPermissions("user:export")
    public void export(PageRequest pageRequest, SysUser user, HttpServletResponse response){

    }
}
