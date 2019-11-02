package com.freeman.common.auth.shiro.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.freeman.common.auth.shiro.token.LoginPlatform;
import com.freeman.common.utils.YamlUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@SuppressWarnings("Duplicates")
@Slf4j
public class JwtUtil {
    private static Long t = Optional.ofNullable(Long.valueOf(YamlUtil.get("freeman.jwtTimeOut"))).map(Long::longValue).orElse(-1L);
    private static final long jwtTimeOut = t * 60 * 1000; //过期时间(毫秒)

    public static final String ACCOUNT = "userId"; //账户唯一标识,存在JWT载体中的字段名称

    // ====================================签名方法 begin========================================= //
    /**
     * 生成签名,time秒后过期
     *
     * @param userId 用户ID
     * @param secret JWT加密秘钥 //用户的密码
     * @return 加密的token
     */
    /*public static String sign(Long userId, String secret) {
        Map<String, Object> payloadMap = new HashMap(2){{
            put("userId",userId);
            put("LoginPlatform", LoginPlatform.PC.name());
        }};
        return sign(payloadMap,secret);
    }*/
    /**
     * 生成签名,time秒后过期
     *
     * @param userId 用户ID
     * @param loginPlatform 用户登录的设备类型 默认值: Constants.USER.LoginPlatform._PC
     * @param secret JWT加密秘钥 //用户的密码
     * @return 加密的token
     */
    public static String sign(Long userId, LoginPlatform loginPlatform, String secret) {
        Map<String, Object> payloadMap = new HashMap(2);
        payloadMap.put("userId",userId);
        payloadMap.put("loginPlatform",loginPlatform.name());
        return sign(payloadMap,secret);
    }

    /**
     * 生成签名,time秒后过期
     *
     * @param payloadMap 负载数据
     * @param secret JWT加密秘钥 //用户的密码
     * @return 加密的token
     */
    public static String sign(Map<String, Object> payloadMap, String secret) {
        try {
            // 使用秘钥的加密算法
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 设置头部信息
            Map<String, Object> header = new HashMap();
            header.put("typ", "JWT");
            header.put("alg", "HS256");
            // 负载数据
            long currentTimeMillis = System.currentTimeMillis();
            Date date = new Date(currentTimeMillis); // 签发时间 // 生效时间
            Date expiresAt = new Date(System.currentTimeMillis()+jwtTimeOut);// 过期时间
            JWTCreator.Builder builder = JWT.create().withHeader(header).withIssuedAt(date).withNotBefore(date).withExpiresAt(expiresAt);
            // 自定义载体内容 username userId 。。。。。
            for (Map.Entry<String, Object> so : payloadMap.entrySet()) {
                Object value = so.getValue();
                if (value instanceof Boolean){
                    builder.withClaim(so.getKey(), (Boolean)value);
                }
                else if (value instanceof Integer){
                    builder.withClaim(so.getKey(), (Integer)value);
                }
                else if (value instanceof Long){
                    builder.withClaim(so.getKey(), (Long)value);
                }
                else if (value instanceof Double){
                    builder.withClaim(so.getKey(), (Double)value);
                }
                else if (value instanceof String){
                    builder.withClaim(so.getKey(), (String)value);
                }
                else if (value instanceof Date){
                    builder.withClaim(so.getKey(), (Date)value);
                }
            }
            return builder.sign(algorithm);

        } catch (Exception e) {
            log.error("error：{}", e);
            return null;
        }
    }
    // ====================================签名方法 over========================================= //

    /**
     * 校验 token是否正确
     *
     * @param token  令牌
     * @param secret JWT加密秘钥 //用户的密码
     * @return 是否正确 //只是过期,没有其他异常也算正确
     */
    public static boolean verify(String token, String secret) {
        boolean result = false;
        try{
            //根据JWT加密秘钥生成JWT校验器
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    //.withClaim("userId", userId)
                    //.withClaim("username", username)
                    //.withClaim("LoginPlatform", LoginPlatform.name())
                    .build();
            verifier.verify(token);
            log.info("JWT is ok");
            result = true;
        }catch (AlgorithmMismatchException e){
            log.error("JWT 算法不匹配");
        }catch (SignatureVerificationException e){
            log.error("JWT 签名验证异常");
        }catch (TokenExpiredException e){
            //JWT 令牌已过期
            log.info("JWT is ok");
            result = true;
        }catch (InvalidClaimException e){
            log.error("JWT 无效负载");
        }catch (JWTVerificationException e){
            log.error("JWT无效,请重新登录!");
        }catch (Exception e){
            log.error("JWT校验,未知异常!");
        }

        return result;
    }

    // ==================================== 常用方法 ========================================= //

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户名
     */
    public static Long getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asLong();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的登录设备标识
     */
    public static String getLoginPlatform(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("loginPlatform").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * token是否过期
     * @return true 已经过期
     */
    public static boolean isExpired(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().before(Calendar.getInstance().getTime());
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的签发时间
     */
    public static Date getIssuedAt(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getIssuedAt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的过期时间
     */
    public static Date getExpiresAt(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

}
