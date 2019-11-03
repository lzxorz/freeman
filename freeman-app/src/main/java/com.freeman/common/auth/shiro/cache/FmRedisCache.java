package com.freeman.common.auth.shiro.cache;

import com.freeman.common.cache.redis.JedisDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.*;

/** 重写Shiro的Cache操作Redis的增删改查 */
@Slf4j
@Deprecated
public class FmRedisCache<K, V> implements Cache<K, V> {

    private JedisDao redis;
    private RedisSerializer valueSerializer;
    private String keyPrefix;
    private int expire; //秒


    public FmRedisCache(JedisDao redis, RedisSerializer valueSerializer, String keyPrefix, int expire) {
        this.redis = redis;
        this.valueSerializer = valueSerializer;
        this.keyPrefix = keyPrefix;
        this.expire = expire;
    }

/** 缓存的key */
    /*private String getKey(Object key) { return keyPrefix+key; }*/
    /** 获得byte[]型的key */
    private byte[] getKey(K key){
        return (this.keyPrefix + key).getBytes();
    }

    /** 获取缓存 */
    /*@Override public Object get(Object key) throws CacheException { if (!redis.hasKey(this.getKey(key))) { return null; } return redis.get(this.getKey(key)); }*/
    @Override
    public V get(K key) throws CacheException {
        log.debug("shiro redis cacke get key [" + key + "]");
        if (key == null) { return null; }
        else {
            try {
                byte[] rawValue = redis.get(getKey(key));
                @SuppressWarnings("unchecked")
                V value = vdeserialize(rawValue);
                return value;
            } catch (Throwable t) {
                log.error(t.getMessage());
                throw new CacheException(t);
            }
        }
    }

    /** 保存缓存 */
    /*@Override public Object put(Object key, Object value) throws CacheException { return redis.set(this.getKey(key), value, Integer.parseInt(shiroCacheExpireTime)); }*/
    @Override
    public V put(K key, V value) throws CacheException {
        log.debug("shiro redis cacke put [" + key + "]");
        try {
            redis.set(getKey(key), value != null ? vserialize(value) : null, this.expire);
            return value;
        } catch (Throwable t) {
            log.error(t.getMessage());
            throw new CacheException(t);
        }
    }

    /** 移除缓存 */
    /*@Override public Object remove(Object key) throws CacheException { if (!redis.hasKey(this.getKey(key))) { return null; } redis.del(this.getKey(key)); return null; }*/
    @Override
    public V remove(K key) throws CacheException {
        log.debug("shiro redis cacke remove key [" + key + "]");
        try {
            V previous = get(key);
            redis.del(getKey(key));
            return previous;
        } catch (Throwable t) {
            log.error(t.getMessage());
            throw new CacheException(t);
        }
    }

    /** 清空所有缓存 */
    /*@Override public void clear() throws CacheException { }*/
    @Override
    public void clear() throws CacheException {
        log.debug("shiro redis cacke 删除所有元素");
        try {
            Set<byte[]> keys = redis.keys(getKey((K)"*"));
            if (keys != null && keys.size() != 0) {
                for (byte[] key : keys) {
                    redis.del(key);
                }
            }
        } catch (Throwable t) {
            log.error(t.getMessage());
            throw new CacheException(t);
        }
    }


    /**  获取所有的key */
    /*@Override public Set<K> keys() { return null; }*/
    @Override
    public Set<K> keys() {
        log.debug("shiro redis cacke 获取所有的key");
        try {
            Set<byte[]> keys = redis.keys(getKey((K)"*"));
            if (CollectionUtils.isEmpty(keys)) {
                return Collections.emptySet();
            }else{
                Set<K> newKeys = new HashSet<K>();
                for(byte[] key:keys){
                    newKeys.add((K)new String(key));
                }
                return newKeys;
            }
        } catch (Throwable t) {
            log.error(t.getMessage());
            throw new CacheException(t);
        }
    }

    /** 获取所有的value */
    /*@Override public Collection<V> values() { return null; }*/
    @Override
    public Collection<V> values() {
        log.debug("shiro redis cacke 获取所有的value");
        try {
            Set<byte[]> keys = redis.keys(getKey((K)"*"));
            if (!CollectionUtils.isEmpty(keys)) {
                List<V> values = new ArrayList<V>(keys.size());
                for (byte[] key : keys) {
                    @SuppressWarnings("unchecked")
                    V value = get((K)key);
                    if (value != null) {
                        values.add(value);
                    }
                }
                return Collections.unmodifiableList(values);
            } else {
                return Collections.emptyList();
            }
        } catch (Throwable t) {
            log.error(t.getMessage());
            throw new CacheException(t);
        }
    }

    /** 获取所有缓存的个数 */
    /*@Override public int size() { return 0; }*/
    @Override
    public int size() {
        log.debug("shiro redis cacke 获取所有缓存的个数");
        try {
            return redis.dbSize(getKey((K)"*")).intValue();
        } catch (Throwable t) {
            log.error(t.getMessage());
            throw new CacheException(t);
        }
    }


    public byte[] vserialize(V v) throws SerializationException { return valueSerializer.serialize(v); }
    public V vdeserialize(byte[] bytes) throws SerializationException { return (V)valueSerializer.deserialize(bytes); }
}
