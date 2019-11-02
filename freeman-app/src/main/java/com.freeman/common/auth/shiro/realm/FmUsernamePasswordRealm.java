package com.freeman.common.auth.shiro.realm;

import com.freeman.common.auth.shiro.session.FmByteSource;
import com.freeman.common.auth.shiro.token.FmUsernamePasswordToken;
import com.freeman.common.auth.shiro.token.LoginPlatform;
import com.freeman.common.auth.shiro.utils.JwtUtil;
import com.freeman.common.cache.redis.JedisDao;
import com.freeman.common.constants.Constants;
import com.freeman.common.utils.SpringContextUtil;
import com.freeman.sys.domain.SysUser;
import com.freeman.sys.repository.SysUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;

/**
 * 账密登录时,会调用这里的认证
 * 授权 统一调用 JwtRealm的授权逻辑
 */
@Slf4j
public class FmUsernamePasswordRealm extends AuthenticatingRealm /*AuthorizingRealm*/ {

    //预备sessionId放到request header中的属性名
    private String prepSessionId = "prep_sessionId";
    private String prepJwt = "prep_jwt";

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private JedisDao jedisDao;

    /** 可以处理的 Shiro Token 返回true */
    @Override
    public boolean supports(AuthenticationToken token){
        return token instanceof FmUsernamePasswordToken;
    }

    /** 认证 */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        SysUser user = null;

        FmUsernamePasswordToken shiroToken = (FmUsernamePasswordToken) token;

        String username = shiroToken.getUsername();
        // String password = String.valueOf(usernamePasswordToken.getPassword());

        // 若用户不存在，抛出UnknownAccountException异常
        user = userRepository.findByUsername(username);
        // 账号不存在
        if (user == null) {
            throw new UnknownAccountException("账号或密码不正确");
        }

        // 账号锁定
        if (Constants.USER.STATUS_DISABLE.equals(user.getStatus())) {
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }

        // 根据数据库获取的用户信息，来构建AuthenticationInfo对象(通常使用实现类SimpleAuthenticationInfo)并返回
        // （1）principal: 认证的实体信息, 用来区分是哪个用户, 比如: 用户的实体类对象/用户id/phone/email ...
        Object principal = user.getId().toString();
        // （2）credentials: 密码
        Object credentials = user.getPassword();
        // （3）盐值: 用户原始密码加密时添加点儿佐料,安全性更高.
        //           最好每个用户对应不同的盐值,避免相同的原始密码,加密后的密码也相同.
        //           最好单独保存个盐值字段到用户表, 也可以取用户信息中唯一的字段来做为盐值.
        // ByteSource credentialsSalt = FmByteSource.Util.bytes(user.getSalt());
        // （4）realmName：当前realm对象的name，调用父类的getName()方法即可
        String realmName = getName();

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, new FmByteSource(user.getSalt()), realmName);
        return info;
    }


    /** 凭证校验, 没抛异常,说明通过校验 */
    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        CredentialsMatcher cm = this.getCredentialsMatcher();
        if (cm == null) {
            throw new AuthenticationException("必须配置CredentialsMatcher才能在身份验证期间验证凭据");
        }
        if (!cm.doCredentialsMatch(token, info)) {
            log.error("凭证不匹配");
            throw new IncorrectCredentialsException("凭证不匹配, [" + token + "] ");
        }
        genSessionIdAndJWT(token,info);
    }

    /** 生成session和JWT */
    @SuppressWarnings("Duplicates")
    protected void genSessionIdAndJWT(AuthenticationToken token, AuthenticationInfo info) {

        Long userId = Long.valueOf((String)info.getPrincipals().getPrimaryPrincipal());
        LoginPlatform loginPlatform = ((FmUsernamePasswordToken) token).getLoginPlatform();

        // 生成新令牌
        String newJwt = JwtUtil.sign(userId, loginPlatform, info.getCredentials().toString());

        // 设置在线jwt redis hash缓存 key: 前缀+用户ID, field: 平台标识(名称), value: 令牌
        jedisDao.hset(Constants.USER.LOGIN_JWT_PREFIX + userId,  loginPlatform.name(), newJwt);

        log.info("登录成功, JWT过期时间 ==> {}",new SimpleDateFormat("yyyy-DD-mm HH:mm:ss").format(JwtUtil.getExpiresAt(newJwt)));
        HttpServletRequest httpServletRequest = SpringContextUtil.getHttpServletRequest();
        httpServletRequest.setAttribute(prepSessionId, userId); // sessionId,使用的 用户ID // shiro 会根据这个生成 session
        httpServletRequest.setAttribute(prepJwt,newJwt); // JWT // shiro 会把它放到Http响应头
    }


    /**
     * 授权时,会调用JwtRealm,不会调用这里
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
     */


}
