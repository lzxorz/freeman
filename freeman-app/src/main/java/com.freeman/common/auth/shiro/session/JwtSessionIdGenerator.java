package com.freeman.common.auth.shiro.session;

import com.freeman.common.utils.SpringContextUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;

public class JwtSessionIdGenerator implements SessionIdGenerator {

    private String prepSessionId = "prep_sessionId";//FmHashedCredentialsMatcher中放到request header的属性名

    @Override
    public Serializable generateId(Session session) {
        Serializable sessionId = (Serializable) SpringContextUtil.getHttpServletRequest().getAttribute(prepSessionId);
        if (null==sessionId){
            throw new UnknownSessionException("无效令牌,请重新登录"); //登录接口不要传session
        }
        return sessionId;
    }
}
