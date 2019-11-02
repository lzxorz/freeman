package com.freeman.common.log;


import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.fastjson.JSON;
import com.freeman.common.auth.shiro.utils.ShiroUtil;
import com.freeman.common.utils.SpringContextUtil;
import com.freeman.common.utils.network.IPUtil;
import com.freeman.sys.domain.SysLog;
import com.freeman.sys.domain.SysUser;
import com.freeman.sys.service.ISysLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 记录用户操作日志
 */
// @Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private ISysLogService logService;

    @Pointcut("@annotation(com.freeman.common.log.Log)")
    public void pointcut() { }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        long beginTime = System.currentTimeMillis();
        // 执行方法
        result = point.proceed();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        // 获取 request
        HttpServletRequest request = SpringContextUtil.getHttpServletRequest();
        // 获取 用户
        SysUser user = ShiroUtil.getUserInfo();

        // 保存日志
        SysLog log = new SysLog();
        log.setUserId(user.getId());
        log.setUsername(user.getUsername());

        UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
        log.setOs(userAgent.getOs().getName());
        log.setBrowser(userAgent.getBrowser().getName());
        log.setBrowserVersion(userAgent.getVersion());

        // 请求资源
        log.setUri(request.getRequestURI());
        // 设置 IP 地址
        String ip = IPUtil.getIpAddr(request);
        log.setIp(ip);

        // 请求参数
        Map<String, String[]> map = request.getParameterMap();
        log.setParameter(JSON.toJSONString(map));


        log.setTime(time);
        logService.saveLog(point, log);

        return result;
    }

}
