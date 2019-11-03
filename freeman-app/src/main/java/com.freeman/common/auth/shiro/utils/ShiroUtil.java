package com.freeman.common.auth.shiro.utils;

import cn.hutool.core.convert.Convert;
import com.freeman.common.auth.shiro.session.FmRedisSessionManager;
import com.freeman.common.utils.SpringContextUtil;
import com.freeman.sys.domain.SysUser;
import com.freeman.sys.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @author 刘志新
 */
@Slf4j
public class ShiroUtil {

	private static RandomNumberGenerator randomSaltGen = new SecureRandomNumberGenerator();

	@Value("${freeman.password.algorithmName}")
	private static final String ALGORITH_NAME = "md5";

	@Value("${freeman.password.hashIterations}")
	private static final int HASH_ITERATIONS = 2;

	private static int randomByteSize = 8; // 一个Byte占两个字节，此处生成的8字节, 字符串长度为16

	/** 生成随机盐 */
	public static String randomSalt(){
		return randomSaltGen.nextBytes(randomByteSize).toHex();
	}

	// ======================================密码相关======================================== //
	/**
	 * @description 密码加密
	 * @author 刘志新
	 * @email  lzxorz@163.com
	 * @date   19-8-9 下午2:13
	 * @Param  [passwd 明文密码, salt 盐值 可以调randomSalt()获取]
	 * @return 加密后的密码
	 **/
	public static String encrypt(String passwd, String salt) {
		return new SimpleHash(ALGORITH_NAME, passwd, ByteSource.Util.bytes(salt), HASH_ITERATIONS).toHex();
	}

	/** SysUser#assword 有值之后调用 */
	public static void encrypt(SysUser user) {
		user.setSalt(randomSalt());
		user.setPassword(new SimpleHash(ALGORITH_NAME, user.getPassword(), ByteSource.Util.bytes(user.getSalt()), HASH_ITERATIONS).toHex());
	}

	// ======================================会话相关======================================== //

	private static ICacheService cacheService = SpringContextUtil.getBean("cacheService");
	private static FmRedisSessionManager redisSessionManager = SpringContextUtil.getBean(FmRedisSessionManager.class);

	/** 获取当前用户Subject */
	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}
	/** 获取当前用户session */
	public static Session getSession() {
		return getSubject().getSession();
	}
	/** 获取当前用户sessionId */
	public static String getSessionId() {
		return String.valueOf(getSession().getId());
	}
	/** 获取当前用户Ip */
	public static String getIp() {
		return getSession().getHost();
	}
	/** 当前用户登出 */
	public static void logout() {
		getSubject().logout();
	}


	/**
	 *
	 * @description 获取当前用户的ID
	 * @author 刘志新
	 * @email  lzxorz@163.com
	 * @date   19-8-9 下午2:06
	 * @return
	 **/
	public static Long getUserId() {
		Object userId = SecurityUtils.getSubject().getPrincipal();
		return userId!=null ? Convert.toLong(userId) : null;
	}

	public static SysUser getUserInfo() {
		return cacheService.getUserInfo(getUserId());
	}

	/**
	 * 获取当前用户 包含更多信息
	 * @description 拼装 用户的角色, 数据范围, 数据部门集合, 权限标识集合
	 * @author 刘志新
	 * @email  lzxorz@163.com
	 * @date   19-8-8 下午6:53
	 * @return
	 **/
	public static SysUser getCurrentUser() {
		return cacheService.getUser(getUserId());
	}

	// 获取 在线用户
	public static List<Long> getOnlineUser() {
		return cacheService.getOnlineUser();
	}

	// 保存 在线用户
	public static void appendOnlineUser() {
		cacheService.appendOnline(getUserId());
	}

	/**
	 * 根据用户ID 踢用户下线
	 * @author 刘志新
	 * @email  lzxorz@163.com
	 * @date   19-8-9 下午2:06
	 * @Param userId 用户ID
	 * @return
	 **/
	public static void kickOutUser(Long userId){

		Session session = redisSessionManager.retrieveSessionById(userId);
		Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
		if (attribute == null) return;

		DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
		LogoutAware authc = (LogoutAware) securityManager.getAuthenticator();

		// 删除session
		redisSessionManager.removeSessionById(userId.toString());

		// 清理缓存
		authc.onLogout((SimplePrincipalCollection) attribute);
	}

}
