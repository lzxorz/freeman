package com.freeman.common.auth.shiro.session;

import com.freeman.common.auth.shiro.utils.JwtUtil;
import com.freeman.common.utils.SpringContextUtil;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.*;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

public class FmRedisSessionManager extends DefaultSessionManager implements WebSessionManager {
    private static final Logger log = LoggerFactory.getLogger(FmRedisSessionManager.class);
    private String headerToken = "Authorization";
    private String prepSessionId = "prep_sessionId";
    private String prepJwt = "prep_jwt";
    private String isNewToken = "isNewToken";


    /** 认证成功后, 创建 session */
    @Override
    protected Session doCreateSession(SessionContext context) {
        Serializable sessionId = (Serializable) SpringContextUtil.getHttpServletRequest().getAttribute(prepSessionId);
        if (null!=sessionId && this.sessionDAO instanceof FmRedisSessionDAO){
            Session readSession = ((FmRedisSessionDAO)this.sessionDAO).doReadSession(sessionId);
            // 如果有, 使用 原来的session
            if (null!=readSession) return readSession;
        }
        Session session = super.getSessionFactory().createSession(context);
        this.sessionDAO.create(session);
        return session;
    }

    /** 认证成功后, 创建session对象后 */
    @Override
    public void onStart(Session session, SessionContext context) {
        if (!WebUtils.isHttp(context)) {
            log.debug("SessionContext argument is not HTTP compatible or does not have an HTTP request/response pair. No session ID cookie will be set.");
        } else {
            HttpServletRequest request = WebUtils.getHttpRequest(context);
            HttpServletResponse response = WebUtils.getHttpResponse(context);
            Serializable sessionId = session.getId();
            if (sessionId == null) {
                throw new IllegalArgumentException("sessionId cannot be null when persisting for subsequent requests.");
            }

            //response.setHeader(headerToken, sessionId.toString());
            response.setHeader(headerToken, (String) request.getAttribute(prepJwt));
            response.setHeader(isNewToken,String.valueOf(true));
            response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Headers",request.getHeader("Access-Control-Request-Headers"));
            response.setHeader("Access-Control-Expose-Headers", "Authorization,isNewToken,X-Requested-With,Origin,Content-Type,Accept");
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            log.info("Set session ID header for session with userId {}", sessionId.toString());

            request.removeAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_IS_NEW, Boolean.TRUE);
        }
    }

    /** 认证成功后,创建session代理 */
    @Override
    @SuppressWarnings("Duplicates")
    protected Session createExposedSession(Session session, SessionContext context) {
        if (!WebUtils.isWeb(context)) {
            return super.createExposedSession(session, context);
        } else {
            ServletRequest request = WebUtils.getRequest(context);
            ServletResponse response = WebUtils.getResponse(context);
            SessionKey key = new WebSessionKey(session.getId(), request, response);
            return new DelegatingSession(this, key);
        }
    }

    /** 其他请求,创建session代理 */
    @Override
    @SuppressWarnings("Duplicates")
    protected Session createExposedSession(Session session, SessionKey key) {
        if (!WebUtils.isWeb(key)) {
            return super.createExposedSession(session, key);
        } else {
            ServletRequest request = WebUtils.getRequest(key);
            ServletResponse response = WebUtils.getResponse(key);
            Serializable sessionId = session.getId()==null ? key.getSessionId() : session.getId();
            SessionKey sessionKey = new WebSessionKey(sessionId, request, response);
            return new DelegatingSession(this, sessionKey);
        }
    }

    /** 检索 session */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = this.getSessionId(sessionKey);
        if (sessionId == null) {
            log.debug("Unable to resolve session ID from SessionKey [{}].  Returning null to indicate a session could not be found.", sessionKey);
            return null;
        } else {

            ServletRequest request = null;
            if (sessionKey instanceof WebSessionKey) {
                request = ((WebSessionKey) sessionKey).getServletRequest();
            }

            if (request != null) {
                Object obj = request.getAttribute(sessionId.toString());
                if (obj != null) {
                    log.debug("retrieve session from request");
                    return (Session) obj;
                }
            }

            Session session = this.sessionDAO.readSession(sessionId);

            if (session == null) {
                String msg = "Could not find session with ID [" + sessionId + "]";
                throw new UnknownSessionException(msg);
            } else {
                if (request != null) {
                    request.setAttribute(sessionId.toString(), session);
                }
                return session;
            }
        }
    }

    /** 获取sessionid */
    @Override
    protected Serializable getSessionId(SessionKey key) {
        Serializable id = key.getSessionId();
        if (id == null && WebUtils.isWeb(key)) {
            id = this.getReferencedSessionId(WebUtils.getRequest(key), WebUtils.getResponse(key));
            if (key instanceof DefaultSessionKey){
                ((DefaultSessionKey)key).setSessionId(id);
            }
        }
        return id;
    }

    /** 获取sessionid, 请求头中获取sessionId,并把sessionId放入response中 */
    private Serializable getReferencedSessionId(ServletRequest request, ServletResponse response) {
        if (!(request instanceof HttpServletRequest)) {
            log.debug("Current request is not an HttpServletRequest - cannot get session ID cookie.  Returning null.");
            return null;
        }
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        // 请求头中获取sessionId,并把sessionId放入response中
        String sessionId = getAuthzHeader(request);
        log.debug("Current session ID is {}", sessionId);

        if (null != sessionId && !httpRequest.getRequestURI().contains("/logout")) {
            httpResponse.setHeader(headerToken, sessionId);
            //DefaultWebSessionManager 中代码 直接copy过来
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "header");
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sessionId);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
        }
        // 不把sessionid拼在url后面
        request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, Boolean.FALSE);

        //return sessionId;
        if (null != sessionId){
            Long userId = JwtUtil.getUserId(sessionId);
            if (userId == null) {
                log.error("Current JWT token is {}", sessionId); // 没有用户ID
                throw new IllegalArgumentException("The JWT token is missing the required data.");
            }
            return userId;
        }
        return null;
    }

    /** 过期时,调用 */
    @Override
    protected void onExpiration(Session s, ExpiredSessionException ese, SessionKey key) {
        //super.onExpiration(s, ese, key);
        this.sessionDAO.delete(s); // dao删除
        super.notifyExpiration(s); // 通知监听器session已经过期
        this.onInvalidation(key);  // request移除 sessionid 有效标记, response 移除 sessionid
    }

    /** 失效时,调用 */
    @Override
    protected void onInvalidation(Session session, InvalidSessionException ise, SessionKey key) {
        //super.onInvalidation(session, ise, key);
        if (ise instanceof ExpiredSessionException) {
            this.onExpiration(session, (ExpiredSessionException)ise, key);
        } else {
            this.sessionDAO.delete(session); // dao删除
            super.notifyStop(session); // 通知监听器 session已经销毁
            this.onInvalidation(key);
        }
    }

    /** session销毁 */
    @Override
    protected void onStop(Session session, SessionKey key) {
        //super.onStop(session, key);

        // redis中根据id删除 session
        this.sessionDAO.delete(session);
        this.notifyStop(session);
        // response中删除Session
        if (WebUtils.isHttp(key)) {
            log.debug("Session has been stopped (subject logout or explicit stop).  response中删除Session.");
            this.removeSessionIdHeader(WebUtils.getHttpRequest(key), WebUtils.getHttpResponse(key));
        } else {
            log.debug("SessionKey argument is not HTTP compatible or does not have an HTTP request/response pair. Session ID cookie will not be removed due to stopped session.");
        }
    }


    /** 过期时、失效时, request移除 sessionid 有效标记, response 移除 sessionid */
    private void onInvalidation(SessionKey key) {
        ServletRequest request = WebUtils.getRequest(key);
        if (request != null) {
            request.removeAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID);
        }

        if (WebUtils.isHttp(key)) {
            log.debug("Referenced session was invalid.  Removing session ID header.");
            this.removeSessionIdHeader(WebUtils.getHttpRequest(key), WebUtils.getHttpResponse(key));
        } else {
            log.debug("SessionKey argument is not HTTP compatible or does not have an HTTP request/response pair. Session ID cookie will not be removed due to invalidated session.");
        }
    }

    /** response移除sessionid */
    private void removeSessionIdHeader(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader(headerToken, null); //"deleteMe"
    }

    /** request中获取sessionid */
    private String getAuthzHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return httpRequest.getHeader(headerToken);
    }

    /** 外部调用,根据ID检索session */
    public Session retrieveSessionById(Serializable sessionId) {
        Session session = this.sessionDAO.readSession(sessionId);
        return session;
    }
    /** 外部调用,根据ID移除session */
    public void removeSessionById(Serializable sessionId) {
        SimpleSession session = new SimpleSession();
        session.setId(sessionId);
        // redis中根据id删除 session
        this.sessionDAO.delete(session);
        this.notifyStop(session);
        HttpServletRequest request = SpringContextUtil.getHttpServletRequest();
        HttpServletResponse response = SpringContextUtil.getHttpServletResponse();
        request.removeAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID);
        response.setHeader(headerToken, null); //"deleteMe"
    }

    /** 设置request中sessionid(令牌)标识 */
    public void setHeaderToken(String headerToken) { this.headerToken = headerToken; }
    /**  */
    public boolean isServletContainerSessions() { return false; }
}
