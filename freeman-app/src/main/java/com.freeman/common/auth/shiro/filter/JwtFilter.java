package com.freeman.common.auth.shiro.filter;


import com.freeman.common.auth.shiro.token.JwtToken;
import com.freeman.common.auth.shiro.token.LoginPlatform;
import com.freeman.common.auth.shiro.utils.JwtUtil;
import com.freeman.common.auth.shiro.utils.ShiroUtil;
import com.freeman.common.utils.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {
    private String headerToken = "Authorization"; // not authentication
    private String isNewToken = "isNewToken";

    /** 对跨域提供支持 */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        //request.setAttribute(DefaultSubjectContext.SESSION_CREATION_ENABLED, Boolean.FALSE);
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization,isNewToken,X-Requested-With,Origin,Content-Type,Accept");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) { //对于OPTION请求做拦截, 不做token校验
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**  是否允许访问. 返回true则继续，返回false则会调用onAccessDenied()。 */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // isLoginAttempt 是否尝试(JWT token)登录 检测Header里面是否包含Authorization字段，有就进行Token登录认证授权

        String authzHeader = this.getAuthzHeader(request);
        if(null == authzHeader){
            log.error("获取令牌失败");
            return false;
        }
        String loginPlatform = JwtUtil.getLoginPlatform(authzHeader);
        AuthenticationToken jwtToken = new JwtToken(authzHeader, LoginPlatform.valueOf(loginPlatform));
        try {
            /** 提交JwtToken给JwtRealm进行认证, 没抛异常、说明登录成功, 返回true */
            getSubject(request, response).login(jwtToken);
            // 保存到在线用户
            if (StrUtil.isNotBlank(WebUtils.toHttp(response).getHeader(isNewToken))) {
                ShiroUtil.appendOnlineUser();
            }
            return true;
        } catch (Exception e) {
            //log.error("认证失败...{}",e.getMessage());
            return false;
        }
    }

    /**
     * 访问被拒后(isAccessAllowed()方法中返回false) ,则会进入这个方法。我们这里直接返回错误的response
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        return sendChallenge(servletRequest, servletResponse);
    }

    @Override
    protected boolean sendChallenge(ServletRequest request, ServletResponse response) {
        log.error("认证出现异常, 响应401......");
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpResponse.setContentType("application/json;charset=utf-8");

        try {
            httpResponse.getWriter().write("{\"code\":401,\"message\":\"令牌失效,请重新登录\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    // ====================================================== //
    /** request中获取sessionid */
    @Override
    protected String getAuthzHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return httpRequest.getHeader(this.headerToken);
    }

    /** 设置request中sessionid(令牌)标识 */
    public void setHeaderToken(String headerToken) { this.headerToken = headerToken; }

}
