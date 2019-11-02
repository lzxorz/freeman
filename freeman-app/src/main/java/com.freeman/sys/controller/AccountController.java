package com.freeman.sys.controller;

import com.freeman.common.limit.Limit;
import com.freeman.common.auth.shiro.token.FmUsernamePasswordToken;
import com.freeman.common.auth.shiro.token.LoginPlatform;
import com.freeman.common.auth.shiro.utils.JwtUtil;
import com.freeman.common.auth.shiro.utils.ShiroUtil;
import com.freeman.common.result.R;
import com.freeman.common.cache.redis.JedisDao;
import com.freeman.common.constants.Constants;
import com.freeman.sys.domain.SysLoginLog;
import com.freeman.sys.domain.SysUser;
import com.freeman.sys.repository.SysLoginLogRepository;
import com.freeman.sys.service.ISysLoginLogService;
import com.freeman.sys.service.ISysUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.util.*;

@Validated
@RestController
@RequestMapping("/sys")
public class AccountController {

    @Autowired
    private JedisDao jedisDao;
    // @Autowired
    // private ICacheService cacheService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysLoginLogService loginLogService;
    @Autowired
    private SysLoginLogRepository loginLogRepository;



    /** 登录接口 */
    @PostMapping("/login")
    @Limit(key = "login", period = 60, count = 20, name = "用户名密码登录", prefix = "limit")
    public R login(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password, HttpServletRequest request, HttpServletResponse response) {

        Subject subject = ShiroUtil.getSubject();

        if (!subject.isAuthenticated()) {
            //  登录平台标识 设置 PC
            FmUsernamePasswordToken shiroToken = new FmUsernamePasswordToken(username.toLowerCase(), password.toCharArray(),LoginPlatform.PC);
            subject.login(shiroToken);
        }

        SysUser loginUser = ShiroUtil.getCurrentUser();
        // 更新用户登录时间
        userService.updateLoginTime(loginUser.getId());
        // 保存登录记录
        loginLogService.save(SysLoginLog.builder().userId(loginUser.getId()).username(username).build());

        // 保存到在线用户
        ShiroUtil.appendOnlineUser();

        return R.ok("登录成功", loginUser);
    }

    /** home页 数据 */
    @GetMapping("/index/{userId}")
    public R index(@PathVariable("userId") Long userId) {
        Map<String, Object> data = new HashMap<>();
        // 获取系统访问记录
        Long totalVisitCount = loginLogRepository.findTotalVisitCount();
        data.put("totalVisitCount", totalVisitCount);
        Long todayVisitCount = loginLogRepository.findTodayVisitCount();
        data.put("todayVisitCount", todayVisitCount);
        Long todayIp = loginLogRepository.findTodayIp();
        data.put("todayIp", todayIp);
        // 获取近期系统访问记录
        List<Map<String, Object>> lastSevenVisitCount = loginLogRepository.findLastSevenDaysVisitCount(null);
        data.put("lastSevenVisitCount", lastSevenVisitCount);
        SysUser param = new SysUser();
        param.setId(userId);
        List<Map<String, Object>> lastSevenUserVisitCount = loginLogRepository.findLastSevenDaysVisitCount(param);
        data.put("lastSevenUserVisitCount", lastSevenUserVisitCount);
        return R.ok(data);
    }

    /** 根据用户ID获取在线用户 */
    @RequiresPermissions("user:online")
    @GetMapping("online")
    public R onlineUser() {
        List<Long> onlineUserId = ShiroUtil.getOnlineUser();
        List<SysLoginLog> onlineUser = loginLogService.findOnlineUserByUserId(onlineUserId);
        return R.ok(onlineUser);
    }


    /** 退出,只是删除reids 中的 在线jwt缓存 */
    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String jwt) {
        Long userId = JwtUtil.getUserId(jwt);
        String loginPlatform = JwtUtil.getLoginPlatform(jwt); // 平台标识(名称)
        jedisDao.hdel(Constants.USER.LOGIN_JWT_PREFIX + userId, loginPlatform);

        // 调用 Shiro的退出, 会删掉session, 如果是允许用户多平台同时在线的话, 找不到session 其他平台的令牌无法刷新。
        // ShiroUtil.logout();
    }

    /**
     * 根据用户ID 踢用户下线
     * @author 刘志新
     **/
    @DeleteMapping("/kickout/{userId}")
    public void  kickOutUser(@PathVariable Long userId){
        ShiroUtil.kickOutUser(userId);
    }

    /**  */
    @PostMapping("regist")
    public void regist(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) {
        userService.regist(username, password);
    }
}
