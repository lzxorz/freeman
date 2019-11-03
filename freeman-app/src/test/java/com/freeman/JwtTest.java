package com.freeman;

import cn.hutool.core.util.ArrayUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.freeman.common.cache.redis.JedisDao;
import com.freeman.common.utils.SpringContextUtil;
import com.freeman.spring.data.domain.Model;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Header.Payload.Signature
 *
 * Header（头部）
 *      {
 *       "alg": "HS256",
 *       "typ": "JWT"
 *      }
 *
 * Payload（负载）,官方参考字段(完全可以不用)
 *      iss (issuer)：签发人
 *      exp (expiration time)：过期时间
 *      sub (subject)：主题
 *      aud (audience)：受众
 *      nbf (Not Before)：生效时间
 *      iat (Issued At)：签发时间
 *      jti (JWT ID)：编号
 *
 * Signature（签名）,算法
 *      HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload),secret);
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtTest {

    @Autowired
    private static ApplicationContext applicationContext;

    @BeforeClass
    public static void init() {
        Model.setApplicationContext(applicationContext);
    }

    private static final long EXPIRE_TIME = 5 * 60 * 1000;

    /**
     * 生成签名，5分钟过期
     * @param userId
     * @param username
     * @param secret JWT加密秘钥 嗯,就用用户的password吧
     * @return
     */
    public static String sign(Long userId, String username, String secret) {
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            // 返回token字符串
            return JWT.create()
                    .withHeader(header)
                    .withClaim("userId", userId)
                    .withClaim("username", username)
                    .withExpiresAt(date)
                    .withNotBefore(new Date())
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检验token是否正确
     * @param token 令牌
     * @param secret JWT加密秘钥
     * @return
     */
    public static boolean verify(String token, String secret){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    //.withClaim("userId", userId) //载体的字段 也可用传进来
                    //.withClaim("username", username)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    /**
     * 从token中获取username信息
     * @param **token**
     * @return
     */
    public static String getUsername(String token){
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e){
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void test001() {
        /*Long userId = 1L;
        String username = "lzxorz";
        String password = "123456";
        // shiro 登录。。。
        String token = sign(userId, username, password);
        log.info(token); // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjI0NzA4MTUsInVzZXJJZCI6MSwidXNlcm5hbWUiOiJsenhvcnoifQ.yM2KGk7Yn-W_6eF5YGfU1Sdriee7hI2BaSDUBcvT3Qk
        boolean verify = verify(token,password);
        String getUsername = getUsername(token); // lzxorz
        log.info(getUsername);*/
        String[] jedisRcord = {null,"aaa",null,"bbb"};
        String join = ArrayUtil.join(ArrayUtil.removeNull(jedisRcord), ",");
        System.out.println(join);
    }

    /**
     * 返回 1 ==> 正常访问,正常返回数据即可(其他请求已经更新过令牌,平滑过渡期间)
     * 返回 0 ==> 正常访问,正常返回数据,顺便生成新令牌放到response的header中
     * 返回 null ==> 令牌失效,需要重新登录
     */
    @Test
    public void test002() {
        JedisDao jedisDao = (JedisDao) SpringContextUtil.getBean("jedisDao");
        /*String luaScript =  "if(redis.call('get', KEYS[1]) == ARGV[1]) " +
                            "then " +
                                "return '1' " +
                            "else " +
                                "return redis.call('set',KEYS[1],ARGV[1],'NX','EX',120) " +
                            "end";
        *//** 刷新key的过期时间 **//*
        Object res = jedisDao.getJedis().eval(luaScript, 1, "10086", "aa.bb.cc");*/


        String luaScript =  "-- 检查hash field（用户ID,客户端类型）\n" +
                "local tokens = redis.call('hget',KEYS[1],KEYS[2])\n" +
                "-- 取到的tokens为空,就保存(用户ID,客户端类型,传参令牌1),然后返回‘SUCCESS’\n" +
                "if(not(tokens)) then\n" +
                "    redis.call('hset',KEYS[1],KEYS[2],ARGV[1])\n" +
                "    return 'SUCCESS'\n" +
                "--  取到的tokens不为空\n" +
                "else\n" +
                "    -- 判断取到的tokens,是否包含传参令牌1\n" +
                "    local index = string.find(tokens,ARGV[1]) \n" +
                "    -- 不包含传参令牌1, 返回'NO'\n" +
                "    if(type(index)=='nil') then return 'NO'\n" +
                "    -- 包含则说明传参令牌1可以更新令牌\n" +
                "    else\n" +
                "        -- 删掉传参令牌1\n" +
                "        if(1==index) then\n" +
                "            if((#ARGV[1]+1)<#tokens) then \n" +
                "                tokens = string.gsub(tokens,ARGV[1]..',','')\n" +
                "            else\n" +
                "                tokens = string.gsub(tokens,ARGV[1],'')\n" +
                "            end\n" +
                "        else\n" +
                "            tokens = string.gsub(tokens,','..ARGV[1],'')\n" +
                "        end\n" +
                "        -- 加入传参令牌2(新令牌),没传新令牌返回nil\n" +
                "        if(not(ARGV[2])) then return nil end\n" +
                "        tokens = tokens..','..ARGV[2]\n" +
                "        redis.call('hset',KEYS[1],KEYS[2],tokens)\n" +
                "        return 'SUCCESS'\n" +
                "    end\n" +
                "end";
        /** 刷新key的过期时间 **/
        Object res = jedisDao.getJedis().eval(luaScript, 2, "10086","ALL","aaa.bbb.ccc");

        // 先检查 session 是否存在，存在则可以刷新，，，，，再检查hash（userId=>All=>tokenList）,取到的tokenList是否包含传参tokan，包含则说明这个token可以以旧换新,

        System.out.println(res);
    }





}
