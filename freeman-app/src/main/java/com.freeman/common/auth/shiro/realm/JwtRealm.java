package com.freeman.common.auth.shiro.realm;


import cn.hutool.core.util.StrUtil;
import com.freeman.common.auth.shiro.session.FmRedisSessionDAO;
import com.freeman.common.auth.shiro.token.JwtToken;
import com.freeman.common.auth.shiro.token.LoginPlatform;
import com.freeman.common.auth.shiro.utils.JwtUtil;
import com.freeman.common.cache.redis.JedisDao;
import com.freeman.common.constants.Constants;
import com.freeman.common.utils.SpringContextUtil;
import com.freeman.sys.domain.SysUser;
import com.freeman.sys.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Set;

/**
 * 自定义实现 JwtRealm
 *
 * @author 刘志新
 */
@Slf4j
public class JwtRealm extends AuthorizingRealm {

    private String headerToken = "Authorization";
    //预备sessionId放到request header中的属性名
    private String prepSessionId = "prep_sessionId";
    private String prepJwt = "prep_jwt";
    private String isNewToken = "isNewToken";

    @Autowired
    private ICacheService cacheService;
    @Autowired
    private JedisDao jedisDao;
    @Autowired
    private SessionDAO sessionDAO;

    @Override
    public boolean supports(AuthenticationToken token) { return token instanceof JwtToken; }

    /**
     * 用户认证,不缓存
     *
     * @return AuthenticationInfo 身份认证信息
     * @throws AuthenticationException 认证相关异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // jwtToken JwtFilter#executeLogin 方法传递过来的
        JwtToken jwtToken = (JwtToken) authenticationToken;

        Long userId = JwtUtil.getUserId(jwtToken.getToken());
        if (null == userId) {
            log.error("JWT 用户ID为空");
            throw new AuthenticationException("JWT无效,请重新登录!");
        }

        SysUser user = cacheService.getUser(userId);
        if (user == null) {
            log.error("帐号不存在");
            throw new UnknownAccountException("用户不存在");
        }

        if (Constants.USER.STATUS_DISABLE.equals(user.getStatus())) {
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }

        return new SimpleAuthenticationInfo(user.getId().toString(), user.getPassword(), getName());
    }


    /** 凭证校验 */
    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) token;
        String jwtStr = jwtToken.getToken();
        Long userId = Long.valueOf((String)info.getPrincipals().getPrimaryPrincipal());
        String password = info.getCredentials().toString();

        // JWT校验不通过(只是过期,不影响校验通过)  或 session不存在
        if (!JwtUtil.verify(jwtStr, password) || null == ((FmRedisSessionDAO)sessionDAO).doReadSession(userId)){
            log.error("JWT校验不通过 或 session不存在...");
            throw new AuthenticationException("JWT失效,请重新登录!");
        }

        // 平滑过渡令牌 (以旧换新时, 旧令牌 在redis中保存string缓存 key是旧令牌, value是啥不重要, 30秒过期)
        if(StrUtil.isNotBlank(jedisDao.get(jwtStr))){
            return; // 不抛异常(凭证校验通过),不用刷新令牌, 该干嘛干嘛
        }

        // JWT校验通过,session还存在,不是平滑过渡令牌,只是令牌过期, 需要 刷新令牌
        if(JwtUtil.isExpired(jwtStr)){
            log.info("尝试刷新令牌------begin------");
            refreshSessionIdAndJWT(jwtToken,userId,password);
        }
    }

    /** 尝试 更新session, 刷新JWT
     * 一个登录状态的令牌有且只有一次刷新令牌的权限(从reids缓存中 以旧换新,lua脚本实现原子操作; 刚被换下来的旧令牌 存30秒临时缓存, 临时缓存有效期内照常访问, 实现用户无感知平滑过渡 )
     */
    @SuppressWarnings("Duplicates")
    protected void refreshSessionIdAndJWT(JwtToken jwtToken, Long userId, String password) {

        String oldJwt = jwtToken.getToken(); // 旧令牌
        LoginPlatform loginPlatform = jwtToken.getLoginPlatform();
        String newjwt = JwtUtil.sign(userId, loginPlatform, password); // 生成新令牌

        //刷新令牌(以旧换新)  lua脚本操作redis实现原子操作, 刷新时,再给旧令牌 生成平滑过渡令牌 续命30秒
        // hashKey: loginJwtPrefix + userId, field: 登录平台的标识, value: 刚生成的新令牌.  返回 true 刷新成功
        if(jedisDao.tryRefreshToken(Constants.USER.LOGIN_JWT_PREFIX + userId, loginPlatform.name(), oldJwt, newjwt)){
            log.info("刷新令牌成功, JWT过期时间 ==> {}",new SimpleDateFormat("yyyy-DD-mm HH:mm:ss").format(JwtUtil.getExpiresAt(newjwt)));
            HttpServletRequest request = SpringContextUtil.getHttpServletRequest();
            HttpServletResponse response = SpringContextUtil.getHttpServletResponse();

            response.setHeader(headerToken, newjwt);
            response.setHeader(isNewToken,String.valueOf(true)); // 前端 根据http响应头 isNewToken为true , 更新前端保存的JWT
            response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Headers",request.getHeader("Access-Control-Request-Headers"));
            response.setHeader("Access-Control-Expose-Headers", "Authorization,isNewToken,X-Requested-With,Origin,Content-Type,Accept");
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }else if(StrUtil.isBlank(jedisDao.get(oldJwt))) {
            log.error("刷新令牌失败, 且不是过渡令牌");
            throw new AuthenticationException("JWT失效,请重新登录!");
        }
    }


    /**
     * 授权模块，获取用户角色和权限
     *
     * @return AuthorizationInfo 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        Long userId = Long.valueOf((String)principals.getPrimaryPrincipal());

        // 获取用户角色集
        Set<String> roles = cacheService.getUserRoleIds(userId);
        simpleAuthorizationInfo.setRoles(roles);

        // 获取用户权限集
        Set<String> permissions = cacheService.getUserPermissions(userId);
        simpleAuthorizationInfo.setStringPermissions(permissions);
        return simpleAuthorizationInfo;
    }


}
