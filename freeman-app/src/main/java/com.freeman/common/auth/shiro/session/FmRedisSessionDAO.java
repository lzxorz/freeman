package com.freeman.common.auth.shiro.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.crazycake.shiro.SessionInMemory;
import org.crazycake.shiro.exception.SerializationException;
import org.crazycake.shiro.serializer.ObjectSerializer;
import org.crazycake.shiro.serializer.RedisSerializer;
import org.crazycake.shiro.serializer.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/** 复制Shiro-redis的RedisSessionDAO --修改一下访问权限--> public Session doReadSession(Serializable sessionId) */
public class FmRedisSessionDAO extends AbstractSessionDAO {

    private static Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);
    private static final String DEFAULT_SESSION_KEY_PREFIX = "shiro:session:";
    private String keyPrefix = "shiro:session:";
    private static final long DEFAULT_SESSION_IN_MEMORY_TIMEOUT = 1000L;
    private long sessionInMemoryTimeout = 1000L;
    private static final boolean DEFAULT_SESSION_IN_MEMORY_ENABLED = true;
    private boolean sessionInMemoryEnabled = true;
    private static final int DEFAULT_EXPIRE = -2;
    private static final int NO_EXPIRE = -1;
    private int expire = -2;
    private static final int MILLISECONDS_IN_A_SECOND = 1000;
    private IRedisManager redisManager;
    private org.crazycake.shiro.serializer.RedisSerializer keySerializer = new StringSerializer();
    private org.crazycake.shiro.serializer.RedisSerializer valueSerializer = new ObjectSerializer();
    private static ThreadLocal sessionsInThread = new ThreadLocal();


    public void update(Session session) throws UnknownSessionException {
        this.saveSession(session);
        if (this.sessionInMemoryEnabled) {
            this.setSessionToThreadLocal(session.getId(), session);
        }

    }

    private void saveSession(Session session) throws UnknownSessionException {
        if (session != null && session.getId() != null) {
            byte[] key;
            byte[] value;
            try {
                key = this.keySerializer.serialize(this.getRedisSessionKey(session.getId()));
                value = this.valueSerializer.serialize(session);
            } catch (org.crazycake.shiro.exception.SerializationException var5) {
                logger.error("serialize session error. session userId=" + session.getId());
                throw new UnknownSessionException(var5);
            }

            if (this.expire == -2) {
                this.redisManager.set(key, value, (int)(session.getTimeout() / 1000L));
            } else {
                if (this.expire != -1 && (long)(this.expire * 1000) < session.getTimeout()) {
                    logger.warn("Redis session expire time: " + this.expire * 1000 + " is less than Session timeout: " + session.getTimeout() + " . It may cause some problems.");
                }

                this.redisManager.set(key, value, this.expire);
            }
        } else {
            logger.error("session or session userId is null");
            throw new UnknownSessionException("session or session userId is null");
        }
    }

    public void delete(Session session) {
        if (session != null && session.getId() != null) {
            try {
                this.redisManager.del(this.keySerializer.serialize(this.getRedisSessionKey(session.getId())));
            } catch (org.crazycake.shiro.exception.SerializationException var3) {
                logger.error("delete session error. session userId=" + session.getId());
            }

        } else {
            logger.error("session or session userId is null");
        }
    }

    public Collection<Session> getActiveSessions() {
        HashSet sessions = new HashSet();

        try {
            Set<byte[]> keys = this.redisManager.keys(this.keySerializer.serialize(this.keyPrefix + "*"));
            if (keys != null && keys.size() > 0) {
                Iterator var3 = keys.iterator();

                while(var3.hasNext()) {
                    byte[] key = (byte[])var3.next();
                    Session s = (Session)this.valueSerializer.deserialize(this.redisManager.get(key));
                    sessions.add(s);
                }
            }
        } catch (org.crazycake.shiro.exception.SerializationException var6) {
            logger.error("get active sessions error.");
        }

        return sessions;
    }

    protected Serializable doCreate(Session session) {
        if (session == null) {
            logger.error("session is null");
            throw new UnknownSessionException("session is null");
        } else {
            Serializable sessionId = this.generateSessionId(session);
            this.assignSessionId(session, sessionId);
            this.saveSession(session);
            return sessionId;
        }
    }

    public Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            logger.warn("session userId is null");
            return null;
        } else {
            Session session;
            if (this.sessionInMemoryEnabled) {
                session = this.getSessionFromThreadLocal(sessionId);
                if (session != null) {
                    return session;
                }
            }

            session = null;
            logger.debug("read session from redis");

            try {
                session = (Session)this.valueSerializer.deserialize(this.redisManager.get(this.keySerializer.serialize(this.getRedisSessionKey(sessionId))));
                if (this.sessionInMemoryEnabled) {
                    this.setSessionToThreadLocal(sessionId, session);
                }
            } catch (SerializationException var4) {
                logger.error("read session error. settionId=" + sessionId);
            }

            return session;
        }
    }

    private void setSessionToThreadLocal(Serializable sessionId, Session s) {
        Map<Serializable, SessionInMemory> sessionMap = (Map)sessionsInThread.get();
        if (sessionMap == null) {
            sessionMap = new HashMap();
            sessionsInThread.set(sessionMap);
        }

        this.removeExpiredSessionInMemory((Map)sessionMap);
        SessionInMemory sessionInMemory = new SessionInMemory();
        sessionInMemory.setCreateTime(new Date());
        sessionInMemory.setSession(s);
        ((Map)sessionMap).put(sessionId, sessionInMemory);
    }

    private void removeExpiredSessionInMemory(Map<Serializable, SessionInMemory> sessionMap) {
        Iterator it = sessionMap.keySet().iterator();

        while(it.hasNext()) {
            Serializable sessionId = (Serializable)it.next();
            SessionInMemory sessionInMemory = (SessionInMemory)sessionMap.get(sessionId);
            if (sessionInMemory == null) {
                it.remove();
            } else {
                long liveTime = this.getSessionInMemoryLiveTime(sessionInMemory);
                if (liveTime > this.sessionInMemoryTimeout) {
                    it.remove();
                }
            }
        }

    }

    private Session getSessionFromThreadLocal(Serializable sessionId) {
        if (sessionsInThread.get() == null) {
            return null;
        } else {
            Map<Serializable, SessionInMemory> sessionMap = (Map)sessionsInThread.get();
            SessionInMemory sessionInMemory = (SessionInMemory)sessionMap.get(sessionId);
            if (sessionInMemory == null) {
                return null;
            } else {
                long liveTime = this.getSessionInMemoryLiveTime(sessionInMemory);
                if (liveTime > this.sessionInMemoryTimeout) {
                    sessionMap.remove(sessionId);
                    return null;
                } else {
                    logger.debug("read session from memory");
                    return sessionInMemory.getSession();
                }
            }
        }
    }

    private long getSessionInMemoryLiveTime(SessionInMemory sessionInMemory) {
        Date now = new Date();
        return now.getTime() - sessionInMemory.getCreateTime().getTime();
    }

    private String getRedisSessionKey(Serializable sessionId) {
        return this.keyPrefix + sessionId;
    }

    public IRedisManager getRedisManager() {
        return this.redisManager;
    }

    public void setRedisManager(IRedisManager redisManager) {
        this.redisManager = redisManager;
    }

    public String getKeyPrefix() {
        return this.keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public org.crazycake.shiro.serializer.RedisSerializer getKeySerializer() {
        return this.keySerializer;
    }

    public void setKeySerializer(org.crazycake.shiro.serializer.RedisSerializer keySerializer) {
        this.keySerializer = keySerializer;
    }

    public org.crazycake.shiro.serializer.RedisSerializer getValueSerializer() {
        return this.valueSerializer;
    }

    public void setValueSerializer(RedisSerializer valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public long getSessionInMemoryTimeout() {
        return this.sessionInMemoryTimeout;
    }

    public void setSessionInMemoryTimeout(long sessionInMemoryTimeout) {
        this.sessionInMemoryTimeout = sessionInMemoryTimeout;
    }

    public int getExpire() {
        return this.expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public boolean getSessionInMemoryEnabled() {
        return this.sessionInMemoryEnabled;
    }

    public void setSessionInMemoryEnabled(boolean sessionInMemoryEnabled) {
        this.sessionInMemoryEnabled = sessionInMemoryEnabled;
    }

    public static ThreadLocal getSessionsInThread() {
        return sessionsInThread;
    }
    
}