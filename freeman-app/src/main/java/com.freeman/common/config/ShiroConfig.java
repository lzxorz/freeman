package com.freeman.common.config;

import com.freeman.common.auth.shiro.session.FmRedisSessionDAO;
import com.freeman.common.auth.shiro.session.FmRedisSessionManager;
import com.freeman.common.auth.shiro.session.JwtSessionIdGenerator;
import com.freeman.common.auth.shiro.filter.JwtFilter;
import com.freeman.common.auth.shiro.session.FmSessionListener;
import com.freeman.common.auth.shiro.realm.FmUsernamePasswordRealm;
import com.freeman.common.auth.shiro.realm.JwtRealm;
import com.freeman.common.utils.serializer.FastJsonSerializer;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import redis.clients.jedis.JedisPool;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Shiro 配置类
 *
 * @author 刘志新
 */
@Configuration
@DependsOn("redisConfig")
public class ShiroConfig {

    @Value("${server.servlet.session.timeout}")
    private int sessionTimeout;

    @Value("${freeman.password.algorithmName}")
    private static final String ALGORITH_NAME = "md5";

    @Value("${freeman.password.hashIterations}")
    private static final int HASH_ITERATIONS = 2;

    @Autowired
    private JedisPool jedisPool;

    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     * Filter Chain定义说明
     *  1、 一个URL可以配置多个Filter，使用逗号分隔
     *  2、 当设置多个过滤器时，全部验证通过，才视为通过
     *  3、 部分过滤器可指定参数，如perms，roles
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactory(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 设置 securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // shiroFilterFactoryBean.setLoginUrl("/login");
        // shiroFilterFactoryBean.setSuccessUrl("/index");
        // shiroFilterFactoryBean.setUnauthorizedUrl("/403");

        // 在Shiro过滤器链上添加自己的过滤器 并且取名为jwt
        LinkedHashMap<String, Filter> filters = new LinkedHashMap();
        filters.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filters);


        // 自定义url规则, 自上向下顺序执行,/**放在最为下边
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/sys/login/**", "anon");
        filterChainDefinitionMap.put("/sys/logout", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/swagger**/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/websocket", "anon");
        // 其他所有请求通过我们自己的 JwtFilter
        filterChainDefinitionMap.put("/**", "jwt");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setAuthenticator(authenticator()); // 需要setRealms之前
        // 多个个Realm用于用户登录认证, 一个JwtRealm用于jwt token的认证 和 所有Realm的授权
        securityManager.setRealms(Arrays.asList(jwtRealm(), usernamePasswordRealm()));
        securityManager.setCacheManager(redisCacheManager()); // 设置Cache缓存
        securityManager.setSessionManager(sessionManager()); // 设置Session缓存

        /*// 关闭shiro自带的session，详情见文档
        // http://shiro.apache.org/session-management.html#SessionManagement-StatelessApplications%28Sessionless%29
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultWebSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);*/

        return securityManager;
    }

    /** Realm管理，多realm认证*/
    @Bean
    public Authenticator authenticator() {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        //设置多个realm认证策略，一个成功即跳过其它的
        authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return authenticator;
    }

    /** 用户名密码进行用户身份认证Realm */
    @Bean
    public Realm usernamePasswordRealm(){
        FmUsernamePasswordRealm realm = new FmUsernamePasswordRealm();
        realm.setName("namepwdRealm");   // 赋值个名称,非必要
        //realm.setCachingEnabled(true); // 默认就是个true
        realm.setAuthenticationCachingEnabled(false); // 关掉认证缓存
        realm.setCredentialsMatcher(hashedCredentialsMatcher());
        return realm;
    }
    /** 密码加密算法 */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName(ALGORITH_NAME); //加密算法
        credentialsMatcher.setHashIterations(HASH_ITERATIONS); //加密次数
        return credentialsMatcher;
    }

    /** 用于JWT token认证授权的realm */
    @Bean
    public Realm jwtRealm() {
        JwtRealm realm = new JwtRealm();
        realm.setName("jwtRealm");       // 赋值个名称,非必要
        //realm.setCachingEnabled(true); // 默认就是个true
        realm.setAuthenticationCachingEnabled(false); // 关掉认证缓存
        //realm.setAuthenticationCacheName("authentication");
        realm.setAuthorizationCachingEnabled(true);
        realm.setAuthorizationCacheName("authorization"); // 赋值个名称,非必要
        return realm;
    }


    /** 管理shiro bean生命周期 */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     */
    /*@Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题，https://zhuanlan.zhihu.com/p/29161098
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }*/
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    //===============shiro-redis=====begin=========//
    /** shiro 操作redis */
    @Bean
    @DependsOn("jedisPool")
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setJedisPool(jedisPool);
        return redisManager;
    }
    /** shiro 缓存的管理  */
    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        redisCacheManager.setKeyPrefix("freeman:shiro:cache:");
        redisCacheManager.setValueSerializer(new FastJsonSerializer());
        return redisCacheManager;
    }
    /** shiro 缓存的管理,自定义  */
    @Bean
    public SessionDAO sessionDAO() {
        FmRedisSessionDAO redisSessionDAO = new FmRedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setKeyPrefix("freeman:shiro:session:");
        redisSessionDAO.setValueSerializer(new FastJsonSerializer());
        redisSessionDAO.setSessionIdGenerator(new JwtSessionIdGenerator());
        return redisSessionDAO;
    }
    /** shiro session的管理,自定义 */
    @Bean
    public FmRedisSessionManager sessionManager() {
        FmRedisSessionManager sessionManager = new FmRedisSessionManager();
        sessionManager.setGlobalSessionTimeout(sessionTimeout*60*1000); //全局默认超时时间
        //sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionValidationInterval(sessionTimeout*60*1000); //验证间隔时间
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setSessionListeners(Arrays.asList(new FmSessionListener()));
        return sessionManager;
    }
    //===============shiro-redis=====over=========//


}
